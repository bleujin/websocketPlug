package net.ion.aradon.server;

import net.ion.radon.Options;
import net.ion.radon.core.Aradon;

public class FrontAradonRunner {

	private Options options ;
	public FrontAradonRunner(Options options) {
		this.options = options ; 
	}
	
	public void startAradon() throws Exception{
		Aradon aradon = new Aradon() ;
		aradon.init(options.getString("config", "resource/config/front-aradon-config.xml")) ;
		aradon.startServer(8080) ;
	}
	
	public static void main(String[] args) throws Exception{
		new FrontAradonRunner(new Options(args)).startAradon() ;
	}
}
