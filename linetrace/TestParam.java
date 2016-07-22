package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import ev3Viewer.LogSender;
import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TestParam {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();

		final LogSender ls = new LogSender();

		initializer.init_test(ls);

		while(true){

			calibration();

			int count = 0;

			Sound.beep();

			ls.connect();

			while(true){
				count ++;

				tail.tailThree();

				Delay.msDelay(20);

				if(count == 500){
					break;
				}
			}

			Timer driveTimer = new Timer();
			TimerTask driveTask = new TimerTask(){
				float forward = 0.0F;
				int count = 0;
				long starttime = System.nanoTime();

				public void run(){
					tail.tailTwo();

					//forward += fc.caldelForward();

					forward = 70.0F;
					float turn = tc.calcTurn();

					wmc.setForward(forward);
					wmc.setTurn(turn);

					wmc.controlWheel();


					if(++count > 50){
						float time = (System.nanoTime()-starttime)/1000000;
						ls.addLog("bright", tc.bright, time);
						count = 0;
					}
				}
			};

			driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

			while(true){

				if(Hardware.touchSensorIsPressed() == true){
					driveTimer.cancel();
					Hardware.motorPortL.controlMotor(0, 0);
					Hardware.motorPortR.controlMotor(0, 0);
					ls.send();
					break;
				}
				Delay.msDelay(500);
			}

			boolean flag = false;

			while(true){
				if(Hardware.touchSensorIsPressed() == true){
					flag = true;
				}else{
					if(flag == true){
						break;
					}
				}
				Delay.msDelay(100);
			}

			initializer.reset();
			ls.clear();
			ls.disconnect();

		}


	}

	//黒と白と階段の輝度値を取得して記録しておく
	private static void calibration(){
		BrightTargetKeeper tk = new BrightTargetKeeper();
		BrightMeasure bright = new BrightMeasure();

		boolean flag = false;

		LCD.clear();
		LCD.drawString("Detect BLACK", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		tk.setBlack(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect WHITE", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag =true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		tk.setWhite(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect GRAY", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
		}
		tk.setGray(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect KAIDAN", 0, 0);
		while(flag == false){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
		}
		tk.setStep(bright.measureBrightness());
		LCD.clear();
	}

}