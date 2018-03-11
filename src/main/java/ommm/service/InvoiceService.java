package ommm.service;

import java.util.ArrayList;

import ommm.entities.Invoice;
import ommm.logic.BloomFilter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@EnableAutoConfiguration
@RestController
public class InvoiceService implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    BloomFilter invoiceBloomFilter;
    
    @Autowired
	private SessionFactory sessionFactory;
	
	@RequestMapping(value = "/{id}/IsPaid", method = RequestMethod.GET)
	public Boolean CheckIsPaid(@PathVariable String id){
		System.out.println("input:"+id);
		if(invoiceBloomFilter.contains(id)){
			Session initSession = sessionFactory.openSession();
	    	try {
	    		return initSession.get(Invoice.class, id).isPaid();
	    	} catch(Exception e){
	    		
	    	} finally {
	    		initSession.close();
	    	}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("-------------> init!");
    	Session initSession = sessionFactory.openSession();
    	try {
	    	for(Invoice invoice : ((ArrayList<Invoice>)initSession.createQuery("from Invoice").list())){
	    		invoiceBloomFilter.add(invoice.getId());
	    	}
    	} catch(Exception e){
    		e.printStackTrace();
    	} finally {
    		initSession.close();
    	}
	}
}
