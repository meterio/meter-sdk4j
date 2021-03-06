package com.meter.sdk.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meter.sdk.utils.crypto.ECKey;
import com.meter.sdk.utils.crypto.ExtendedKey;
import com.meter.sdk.utils.crypto.Key;
import com.meter.sdk.utils.crypto.ValidationException;

public final class X509CertificateUtils {

	private static Logger logger = LoggerFactory.getLogger(X509CertificateUtils.class);

	private static CertificateFactory cf;
	static {
		try {
			cf = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
		} catch (CertificateException e) {
			logger.error("init", e);
		}
	}

	/**
	 * Verify certificate signature from given public key.
	 * 
	 * @param certificate
	 * @param publicKey
	 * @return
	 */
	public static boolean verifyCertificateSignature(X509Certificate certificate, byte[] publicKey) {
		if (certificate == null || publicKey == null) {
			return false;
		}
		ThorClientLogger.info("verify public key:" + BytesUtils.toHexString(publicKey, Prefix.ZeroLowerX));
		PublicKey ecPublicKey = createECPublicKeyFromKeyBytes(publicKey);
		if (ecPublicKey == null) {
			return false;
		}
		try {
			certificate.verify(ecPublicKey);
			return true;
		} catch (CertificateException | SignatureException | NoSuchAlgorithmException | InvalidKeyException
				| NoSuchProviderException e) {
			logger.error("verifyCertificateSignature", e);
			return false;
		}
	}

	/**
	 * Verify the certificate from root public key(compressed format).
	 * 
	 * @param certificate certificate object {@link X509Certificate}.
	 * @param rootPubKey  root public key with compressed format.
	 * @param chaincode   chain code.
	 * @return
	 */
	public static boolean verifyCertificateSignature(X509Certificate certificate, byte[] rootPubKey,
			byte[] chaincode) {
		if (certificate == null || rootPubKey == null || chaincode == null) {
			throw new IllegalArgumentException("certificate, rootPubKey or chaincode is Illegal.");
		}
		com.meter.sdk.utils.crypto.ECPublicKey ecPublicKey = new com.meter.sdk.utils.crypto.ECPublicKey(
				rootPubKey);
		ExtendedKey pubKey = new ExtendedKey(ecPublicKey, chaincode, 0, 0, 0);
		int index = index(certificate) & 0x7FFFFFFF;
		Key derivedKey = null;
		try {
			derivedKey = pubKey.derived(index).getMaster();
		} catch (ValidationException e) {
			logger.error("verifyCertificateSignature", e);
		}
		if (derivedKey == null) {
			return false;
		}
		return verifyCertificateSignature(certificate, derivedKey.getRawPublicKey(false));
	}

	/**
	 * Extract public key byte array from {@link X509Certificate}
	 * 
	 * @param certificate
	 * @return 65 bytes uncompressed publickey
	 */
	public static byte[] extractPublicKey(X509Certificate certificate) {
		PublicKey publicKey = certificate.getPublicKey();
		if (publicKey instanceof BCECPublicKey) {
			BCECPublicKey ecPub = (BCECPublicKey) publicKey;
			return ecPub.getQ().getEncoded(false);
		}
		return null;
	}

	/**
	 * Load certificate pem string
	 * 
	 * @param cert pem string.
	 * @return
	 */
	public static X509Certificate loadCertificate(String cert) {
		cert = cert.replace("-----BEGIN CERTIFICATE-----", "");
		cert = cert.replace("-----END CERTIFICATE-----", "");
		cert = cert.replaceAll("\n", "");
		cert = cert.replaceAll("\r", "");
		byte[] certBytes = Base64.getDecoder().decode(cert);
		return parseCertificate(certBytes);
	}

	protected static X509Certificate parseCertificate(byte[] certBytes) {
		if (certBytes == null) {
			return null;
		}
		InputStream inputStream = new ByteArrayInputStream(certBytes);
		X509Certificate certificate = null;
		try {
			certificate = (X509Certificate) cf.generateCertificate(inputStream);
		} catch (CertificateException e) {
			logger.error("parseCertificate", e);
		}
		return certificate;
	}

	/**
	 *
	 * @param keyBytes
	 * @return
	 */
	private static PublicKey createECPublicKeyFromKeyBytes(byte[] keyBytes) {

		ECPoint pubPoint = ECKey.decodeECPoint(keyBytes);
		AlgorithmParameters parameters = null;
		try {
			parameters = AlgorithmParameters.getInstance("EC", "SunEC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("createECPublicKeyFromKeyBytes", e);
			return null;
		}
		try {
			parameters.init(new ECGenParameterSpec("secp256k1"));
		} catch (InvalidParameterSpecException e) {
			logger.error("createECPublicKeyFromKeyBytes", e);
			return null;
		}
		ECParameterSpec ecParameters = null;
		try {
			ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
		} catch (InvalidParameterSpecException e) {
			logger.error("createECPublicKeyFromKeyBytes", e);
			return null;
		}
		ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
		KeyFactory kf = null;
		try {
			kf = KeyFactory.getInstance("EC", "SunEC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("createECPublicKeyFromKeyBytes", e);
			return null;
		}
		try {
			return (ECPublicKey) kf.generatePublic(pubSpec);
		} catch (InvalidKeySpecException e) {
			logger.error("createECPublicKeyFromKeyBytes", e);
			return null;
		}

	}

	/**
	 * Parse index from certificate.
	 * 
	 * @param certificate
	 * @return
	 */
	private static int index(X509Certificate certificate) {
		BigInteger integer = certificate.getSerialNumber();
		byte[] serialNumBytes = integer.toByteArray();
		String serialHex = BytesUtils.toHexString(serialNumBytes, Prefix.ZeroLowerX);
		if (!serialHex.startsWith("0x7eaacc")) {
			throw new IllegalArgumentException("wrong certificate serials number.");
		}
		serialHex = serialHex.replace("0x7eaacc", "");
		int foundIndex = serialHex.indexOf("0d0a");
		if (foundIndex < 0) {
			throw new IllegalArgumentException("wrong certificate serials number.");
		}
		String pathHex = serialHex.substring(0, foundIndex);
		pathHex = pathHex.replace("0d0a", "");
		if (pathHex.length() != 6) {
			throw new IllegalArgumentException("wrong certificate path.");
		}
		if (serialHex.length() < foundIndex + 4) {
			throw new IllegalArgumentException("version format error.");
		}
		String versionHex = serialHex.substring(foundIndex + 4);
		if (!versionHex.equalsIgnoreCase("0001")) {
			throw new IllegalArgumentException("version bytes is illegal.");
		}
		return Integer.parseInt(pathHex, 16);

	}

	/**
	 * Verify transaction signature.
	 * 
	 * @param hexTxHash    hex string format for message hash.
	 * @param hexSignature 32 bytes r| 32 bytes v
	 * @param certificate  {@link X509Certificate}
	 * @return
	 */
	public static boolean verifyTxSignature(String hexTxHash, String hexSignature, X509Certificate certificate) {
		byte[] signature = BytesUtils.toByteArray(hexSignature);

		byte[] txHash = BytesUtils.toByteArray(hexTxHash);
		byte[] pub = X509CertificateUtils.extractPublicKey(certificate);
		if (signature == null || txHash == null || pub == null) {
			throw new IllegalArgumentException("signature, tx hash or certificate is illegal.");
		}
		return ECKey.verify(txHash, signature, pub);
	}

}
