package hackingnight.main;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletAmbientLight.IlluminanceListener;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletMultiTouch.TouchStateListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class ExampleHelloWorld {
	private static final String HOST = "localhost";
	private static final int PORT = 4223;
	private static final String UID = "oct"; // Change to your UID
	private static int tastRate = 1000;

	// Note: To make the example code cleaner we do not handle exceptions. Exceptions you
	//       might normally want to catch are described in the documentation
	public static void main(String args[]) throws Exception {
		IPConnection ipcon = new IPConnection(); // Create IP connection
		BrickletLCD20x4 lcd = new BrickletLCD20x4(UID, ipcon); // Create device object

		ipcon.connect(HOST, PORT); // Connect to brickd
		// Don't use device before ipcon is connected

		// Turn backlight on
		lcd.backlightOn();
		
		

		
		// Write "Hello World"
		BrickletMultiTouch multiTouch = new BrickletMultiTouch("jTu", ipcon);
		
		BrickletAmbientLight lightSensor = new BrickletAmbientLight("m4i", ipcon);
		

		lightSensor.setIlluminanceCallbackPeriod(tastRate);
		
		
		
		
		multiTouch.addTouchStateListener(new TouchStateListener() {
			
			public void touchState(int state) {
				System.out.println("touched "+ state);
				if(state == 6160) {
					tastRate++;
					try {
						lightSensor.setIlluminanceCallbackPeriod(tastRate);
						
						
						


						lcd.writeLine((short) 1,(short) 0, "                                   ");
						lcd.writeLine((short) 1,(short) 0, "Empfindlichkeit: "+tastRate);
						
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(state == 6272) {
					tastRate--;
					try {
						lightSensor.setIlluminanceCallbackPeriod(tastRate);
						
						lcd.writeLine((short) 1,(short) 0, "                                   ");
						lcd.writeLine((short) 1,(short) 0, "Empfindlichkeit: "+tastRate);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		});
		
		
		
		
		
		
		
		
		lcd.clearDisplay();
		lightSensor.addIlluminanceListener(new IlluminanceListener() {
			
			public void illuminance(int illuminance) {
				try {
					lcd.writeLine((short) 0,(short) 0, "                     ");
					lcd.writeLine((short) 0,(short) 0, "actual lux: "+illuminance);
					
					
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		
		
		
		
		

		System.out.println("Press key to exit"); System.in.read();
		ipcon.disconnect();
	}
}
