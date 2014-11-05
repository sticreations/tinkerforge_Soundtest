package hackingnight.main;




import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceListener;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletMultiTouch.TouchStateListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class SoundOscillator {
	
	private static final String HOST = "localhost";
	private static final int PORT = 4223;
	private static final String DISTANCE_UID = "0";
	private static final String DISPLAY_UID = "0";
	private static final String TOUCH_UID = "0";
	
	
	
	private static int OLDPOSITION = 0;
	
	
	public static void main(String args[]) throws  AlreadyConnectedException, IOException, NotConnectedException, TimeoutException, LineUnavailableException {
		
		
		IPConnection ipcon = new IPConnection(); // Create IP connection
		ipcon.connect(HOST, PORT); // Connect
		
		BrickletDistanceIR distance = new BrickletDistanceIR(DISTANCE_UID, ipcon); // Create distance object
		BrickletLCD20x4 lcd = new BrickletLCD20x4(DISPLAY_UID, ipcon); // Create lcd object

		System.out.println(distance.getDistance());
		lcd.backlightOn();
		
		distance.setDistanceCallbackPeriod(100);
		
		

		
		distance.addDistanceListener(new DistanceListener() {   // DistanceListener 
			
			@Override
			public void distance(int distance) {
				// TODO Auto-generated method stub
				play(100+distance*3);
				try {
					lcd.writeLine((short) 0, (short) 0, "                    "); // Clear Line
					
					lcd.writeLine((short)0, (short) 0, "Distance: "+distance);  // Print Distance
				} catch (TimeoutException | NotConnectedException e) {
					e.printStackTrace();
				}
				
				
				int pos = distance/40;   // 20 char Display: Max Distance = 800

				try {
					lcd.writeLine((short) 1,(short) OLDPOSITION, " "); // Clear Old Point on Display
					lcd.writeLine((short) 1,(short) pos , "*");	// Print new Point
				} catch (TimeoutException | NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				OLDPOSITION = pos; // Set Oldposition
				
				
			}
		});
		
		

		System.out.println("Press key to exit"); System.in.read();
		ipcon.disconnect();
		
		
	}
	

	
	
    public static byte[] getSinusTone(int frequency, AudioFormat af) {
        byte sample_size = (byte) (af.getSampleSizeInBits() / 8);
        byte[] data = new byte[(int) af.getSampleRate() * sample_size];
        double step_width = (2 * Math.PI) / af.getSampleRate();
        double x = 0;

        for (int i = 0; i < data.length; i += sample_size) {
            int sample_max_value = (int) Math.pow(2, af.getSampleSizeInBits()) / 2 - 1;
            int value = (int) (sample_max_value * Math.sin(frequency * x));
            for (int j = 0; j < sample_size; j++) {
                byte sample_byte = (byte) ((value >> (8 * j)) & 0xff);
                data[i + j] = sample_byte;
            }
            x += step_width;
        }
        return data;
    }

    public static void play(int frequenzy) {
        AudioFormat af = new AudioFormat(44100, 16, 1, true, false);
        byte[] data = getSinusTone(frequenzy, af);
        try {
            Clip c = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            c.open(af, data, 0, data.length);
            c.start();
            
            while(c.isRunning()) {
                try {
                    Thread.sleep(50);
                } catch (Exception ex) {}
            }
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
}
