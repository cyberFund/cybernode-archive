package fund.cyber.node.web.controller;


import fund.cyber.node.core.chain.DataFetcher;
import fund.cyber.node.core.service.AddressService;
import fund.cyber.node.core.service.BitcoreService;
import fund.cyber.node.core.service.BlockService;
import fund.cyber.node.core.service.TransactionService;
import fund.cyber.node.model.bitcore.Block;
import fund.cyber.node.model.dto.AddressDto;
import fund.cyber.node.model.dto.BlockDto;
import fund.cyber.node.model.dto.StatusDto;
import fund.cyber.node.model.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/")
public class MainController {

    @Autowired
    private BlockService blockService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BitcoreService bitcoreService;

/*
    @Autowired
    private DataFetcher dataFetcher;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public StatusDto getStatus() {
        return dataFetcher.getStatus();
    }


    @RequestMapping(value = "fetch/blocks/start", method = RequestMethod.GET)
    @ResponseBody
    public void startHeaderFetch() {
        dataFetcher.startBlocksFetch();
    }

    @RequestMapping(value = "fetch/blocks/stop", method = RequestMethod.GET)
    @ResponseBody
    public void stopBlocksFetch() {
        dataFetcher.stopBlocksFetch();
    }

    @RequestMapping(value = "fetch/transactions/start", method = RequestMethod.GET)
    @ResponseBody
    public void startTransactionFetch() {
        dataFetcher.startTransactionFetch();
    }

    @RequestMapping(value = "fetch/transactions/stop", method = RequestMethod.GET)
    @ResponseBody
    public void stopTransactionFetch() throws IOException {
        dataFetcher.stopTransactionFetch();
    }
*/


    @RequestMapping(value = "/block/{hash}", method = RequestMethod.GET)
    @ResponseBody
    public Block getBlock(@PathVariable("hash") final String hash) throws IOException {
        return bitcoreService.getBlock(hash);
        //return blockService.get(hash);
    }

    @RequestMapping(value = "/tx/{hash}", method = RequestMethod.GET)
    @ResponseBody
    public TransactionDto getTransaction(@PathVariable("hash") final String hash) throws IOException {
        return transactionService.getTransaction(hash);
    }

    @RequestMapping(value = "/address/{hash}", method = RequestMethod.GET)
    @ResponseBody
    public AddressDto getAddress(@PathVariable("hash") final String hash) throws IOException {
        return addressService.getAddress(hash);
    }

}

