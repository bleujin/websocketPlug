package net.ion.websocket.common;


public class EnumClass {

	public enum Scope {
		Application, Session, Thread, Request ;

		public boolean isApplication() {
			return this == Application;
		}
	}
	
	
	public enum IZone {
		Application {
			public IZone getChildZone(){
				return Section ;
			}
		}, Section{
			public IZone getChildZone(){
				return Section ;
			}
		}, Path{
			public IZone getChildZone(){
				return Section ;
			}
		}, Request {
			public IZone getChildZone(){
				throw new IllegalStateException("request is least scope") ;
			}
		} ;
		
		public abstract IZone getChildZone() ;
	}

}
