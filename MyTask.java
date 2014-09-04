package fiujson;

import java.util.TimerTask;

//JSON Keep alive method


public class MyTask extends TimerTask {
	int currentTime = 0;
	@Override
	public void run() {
		OVSDBClient.jsonKeepAlive();
	
	}

}
