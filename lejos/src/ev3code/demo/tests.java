package ev3code.demo;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class tests {

	
    public static void main(String[] args)
    {
        //Controller controller = new Controller(SensorPort.S4, MotorPort.B, MotorPort.C);
        
        DifferentialDrive d = new DifferentialDrive(MotorPort.A, MotorPort.C);
        

        for (int i = 0; i < 5; i++)
        {
            Delay.msDelay(1000);
        }

        
        d.stop();
        
    }
}
