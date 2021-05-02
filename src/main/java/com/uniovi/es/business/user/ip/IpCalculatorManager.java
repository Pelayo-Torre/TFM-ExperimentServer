package com.uniovi.es.business.user.ip;

import java.util.ArrayList;
import java.util.List;

public class IpCalculatorManager {
	
	private static IpCalculatorManager instance;
	
	private List<IpCalculator> ipCalculators = new ArrayList<IpCalculator>();
	
	private IpCalculatorManager() {}
	
	public static IpCalculatorManager getInstance() {
		if(instance == null) {
			instance = new IpCalculatorManager();
		}
		return instance;
	}

	public List<IpCalculator> getIpCalculators() {
		return ipCalculators;
	}
	
	public void add(IpCalculator ipCalculator) {
		this.ipCalculators.add(ipCalculator);
	}

}
