package fund.cyber.chainparser.web.controller;


import fund.cyber.chainparser.core.chain.DataFetcher;
import fund.cyber.chainparser.core.service.AddressService;
import fund.cyber.chainparser.core.service.BlockChainService;
import fund.cyber.chainparser.core.service.BlockService;
import fund.cyber.chainparser.core.service.TransactionService;
import fund.cyber.chainparser.model.dto.AddressDto;
import fund.cyber.chainparser.model.dto.BlockDto;
import fund.cyber.chainparser.model.dto.StatusDto;
import fund.cyber.chainparser.model.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

    @RequestMapping(value = "/block/{hash}", method = RequestMethod.GET)
    @ResponseBody
    public BlockDto getBlock(@PathVariable("hash") final String hash) throws IOException {
        return blockService.get(hash);
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

