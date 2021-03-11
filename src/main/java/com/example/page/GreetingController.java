package com.example.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingController {
	
	private Producer producer = new Producer();
	
	HS100 plug = new HS100("192.168.0.100");
	
    @Autowired
    GreetingController(Producer producer) {
        this.producer = producer;
    }

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping(
			value = "/process", 
			method = RequestMethod.POST,
			consumes = "text/plain")
	public void process(@RequestBody String payload) throws Exception {

		System.out.println(payload);
		
		if(payload.equals("true")){
			plug.switchOn();
			this.producer.sendMessage(plug.getEnergy());
		}else {
			plug.switchOff();
		}

	}

}