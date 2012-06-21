package net.ion.chat.handler;


public interface Snooper {
	
	public final Snooper NOTDEFINED = new Snooper(){
		public Tracer getTracer(String userId) {
			return Tracer.NONE;
		}};

	public Tracer getTracer(String userId) ;

}
