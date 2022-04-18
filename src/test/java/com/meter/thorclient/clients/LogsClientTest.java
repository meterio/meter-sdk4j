package com.meter.thorclient.clients;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.fastjson.JSONObject;
import com.meter.thorclient.base.BaseTest;
import com.meter.thorclient.core.model.blockchain.Block;
import com.meter.thorclient.core.model.blockchain.EventList;
import com.meter.thorclient.core.model.blockchain.FilteredLogEvent;
import com.meter.thorclient.core.model.blockchain.FilteredTransferEvent;
import com.meter.thorclient.core.model.blockchain.LogFilter;
import com.meter.thorclient.core.model.blockchain.Options;
import com.meter.thorclient.core.model.blockchain.Order;
import com.meter.thorclient.core.model.blockchain.Range;
import com.meter.thorclient.core.model.blockchain.TransferredFilter;
import com.meter.thorclient.core.model.clients.Address;
import com.meter.thorclient.core.model.clients.ERC20Contract;
import com.meter.thorclient.core.model.clients.base.AbiDefinition;
import com.meter.thorclient.core.model.exception.ClientArgumentException;
import com.meter.thorclient.utils.BytesUtils;
import com.meter.thorclient.utils.Prefix;

@RunWith(JUnit4.class)
public class LogsClientTest extends BaseTest {

	@Test
	public void testFilterEvents() throws ClientArgumentException {
		Block block = BlockClient.getBlock(null);

		List<String> eventsTransferInputs = new ArrayList<String>();
		eventsTransferInputs.add("address");
		eventsTransferInputs.add("address");
		eventsTransferInputs.add("uint256");
		AbiDefinition abiDefinition = ERC20Contract.defaultERC20Contract.findAbiDefinition("Transfer", "event",
				eventsTransferInputs);
		String abiMethodHexString = BytesUtils.toHexString(abiDefinition.getBytesMethodHashed(), Prefix.ZeroLowerX);
		logger.info("abi Transfer:" + abiMethodHexString);
		LogFilter logFilter = LogFilter.createFilter(Range.createBlockRange(0, Long.parseLong(block.getNumber())),
				Options.create(0, 10));
		logFilter.setOrder(Order.DESC.getValue());

		logFilter.addTopicSet(Address.MTRG_Address.toHexString(null), abiMethodHexString,
				"0x000000000000000000000000" + fromAddress.substring(2), null, null, null);
		
		ArrayList<FilteredLogEvent> filteredEvents = LogsClient.getFilteredLogEvents(logFilter);
		for (FilteredLogEvent filteredTransferEvent : filteredEvents) {
			logger.info("filteredTransferEvent:{}", JSONObject.toJSONString(filteredTransferEvent));
		}
	}

	@Test
	public void testTransferLogs() throws ClientArgumentException {

		
		Block block = BlockClient.getBlock(null);

		TransferredFilter transferredFilter = TransferredFilter
				.createFilter(Range.createBlockRange(0, Long.parseLong(block.getNumber())), Options.create(0, 10));

		transferredFilter.addTransferCriteria(null, null,
				null);
		
		transferredFilter.setOrder(Order.DESC.getValue());
		ArrayList<FilteredTransferEvent> transferLogs = LogsClient.getFilteredTransferLogs(transferredFilter);
		logger.info("transferLogs:{}", JSONObject.toJSONString(transferLogs));
	}

}
