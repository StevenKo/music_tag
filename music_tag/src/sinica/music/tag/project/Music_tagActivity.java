package sinica.music.tag.project;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import sinica.music.tag.mfcc.MFCC;
import sinica.music.tag.mfcc.MFCC;
import sinica.music.tag.project.WavInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Music_tagActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static WavInfo wavInfo;
	byte[] data;
	
	//music player
	private ImageButton mPause, mStart, mStop;
	private TextView mTextView1; 
	private ImageView mImageView1; 
	private boolean bIsReleased = false;
	private boolean bIsPaused = false; 
	public MediaPlayer myPlayer1 = new MediaPlayer();
	private SeekBar mSeeker;
	int duration;
    Handler handler = new Handler();
    private String[] valArray;
    private double[] valDoubleArray;
    private BufferedReader in;
	private TextView mTag1;
    
    double[] mfcc;
	
	Runnable start=new Runnable(){
		 
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try 
            { 
            
              
              if(myPlayer1.isPlaying()==true) 
              {
                /*ß‚ MediaPlayer≠´≥]*/
                myPlayer1.reset();
                myPlayer1.prepare();
              }
              /*≥]©w MediaPlayer≈™®˙SDcard™∫¿…Æ◊*/
//              myPlayer1.setDataSource( "/sdcard/nana.wav" );
//              
//              mSeeker.setMax(myPlayer1.getDuration());
              
              
              /*ß‚ MediaPlayer∂}©lºΩ©Ò*/
              myPlayer1.start(); 
              mTextView1.setText(R.string.str_start); 
            } 
            catch (IllegalStateException e) 
            { 
              // TODO Auto-generated catch block 
              mTextView1.setText(e.toString()); 
              e.printStackTrace(); 
            } 
            catch (IOException e) 
            { 
              // TODO Auto-generated catch block 
              mTextView1.setText(e.toString()); 
              e.printStackTrace(); 
            } 
             
            /* ∑Ì≠µº÷ºΩßπ∑|∞ı¶Ê™∫Listener */  
            myPlayer1.setOnCompletionListener(new OnCompletionListener() 
            { 
              // @Override 
              public void onCompletion(MediaPlayer arg0) 
              {  
                mTextView1.setText(R.string.str_finished);
                mStart.setImageResource(R.drawable.star);
              } 
            });
			handler.post(updatesb);
			handler.postDelayed(updateTag, 500);
			//用一个handler更新SeekBar
		}
 
    };
    
    Runnable updateTag = new Runnable(){

		@Override
		public void run() {
			handler.postDelayed(updateTag, 100);
			String getLine;
			try {
				getLine = in.readLine();
				valArray = getLine.split("\\s+");
				valDoubleArray = new double[valArray.length-1];
				for(int i=0; i<valDoubleArray.length;i++){
					valDoubleArray[i] = Double.parseDouble(valArray[i+1]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mTag1.setTextSize((float) ((float) Math.exp(valDoubleArray[0])* 15.0));
			Double.parseDouble(valArray[1]);
		}
    	
    };
    
    Runnable updatesb =new Runnable(){
    	 
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mSeeker.setProgress(myPlayer1.getCurrentPosition());
			handler.postDelayed(updatesb, 1000);
			//每秒钟更新一次
		}
 
    };
	private Button mFile;
	
	
    
//    private OnSeekBarChangeListener sbLis=new OnSeekBarChangeListener(){
//    	 
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//			// TODO Auto-generated method stub
//		}
// 
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
// 
//		}
// 
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
//			myPlayer1.seekTo(mSeeker.getProgress());
//			//SeekBar确定位置后，跳到指定位置
//		}
// 
//    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mStart = (ImageButton) findViewById(R.id.myImageButton1); 
        mStop = (ImageButton) findViewById(R.id.myImageButton2);
        mPause = (ImageButton) findViewById(R.id.pause); 
        mImageView1 = (ImageView) findViewById(R.id.myImageView1);
        mTextView1 = (TextView) findViewById(R.id.myTextView1);
        mSeeker = (SeekBar) findViewById(R.id.seek);
        mTag1 = (TextView) findViewById(R.id.tag1);
        mFile = (Button) findViewById(R.id.fileBtn);
        
        
        try {
//			File f = new File(Environment.getExternalStorageDirectory()+"/test.wav");
            setPlayer("/sdcard/test.wav");
		} catch (IllegalArgumentException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalStateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        setFile(Environment.getExternalStorageDirectory()+"/test.txt"); 
        
        
        mFile.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		  		intent.setComponent(new ComponentName("com.nexes.manager", "com.nexes.manager.Main"));
		  		startActivityForResult(intent,11);
				
			}
        	
        });
     
        mStart.setOnClickListener(new ImageButton.OnClickListener() 
        { 
          @Override 
          public void onClick(View v) 
          { 
        	  
  		    
        	  
//              PackageManager pm = getPackageManager();
//              Intent intent = pm.getLaunchIntentForPackage("com.nexes.manager");
////              intent.setAction(Intent.ACTION_GET_CONTENT);
//              startActivityForResult(intent, 1200);
              
        	  handler.post(start);
//            // TODO Auto-generated method stub 
//        	  mStart.setImageResource(R.drawable.stars);
//              mImageView1.setImageResource(R.drawable.dance);
//              mPause.setImageResource(R.drawable.pause);
//              
//              try 
//              { 
//                if(myPlayer1.isPlaying()==true) 
//                {
//                  myPlayer1.reset();            
//                }
//                
//                myPlayer1.prepare();
//                myPlayer1.start(); 
//                mTextView1.setText(R.string.str_start); 
//              } 
//              catch (IllegalStateException e) 
//              { 
//                // TODO Auto-generated catch block 
//                mTextView1.setText(e.toString()); 
//                e.printStackTrace(); 
//              } 
//              catch (IOException e) 
//              { 
//                // TODO Auto-generated catch block 
//                mTextView1.setText(e.toString()); 
//                e.printStackTrace(); 
//              } 
//               
//              myPlayer1.setOnCompletionListener(new OnCompletionListener() 
//              { 
//                // @Override 
//                public void onCompletion(MediaPlayer arg0) 
//                {  
//                  mTextView1.setText(R.string.str_finished);
//                  mStart.setImageResource(R.drawable.star);
//                } 
//              });
            
            
          } 
        });     
         
        /*º»∞±´ˆ∂s */ 
        mPause.setOnClickListener(new ImageButton.OnClickListener() 
        { 
          public void onClick(View view) 
          { 
            if (myPlayer1 != null) 
            { 
              if(bIsReleased == false) 
              { 
                if(bIsPaused==false) 
                { 
                  myPlayer1.pause(); 
                  bIsPaused = true; 
                  mTextView1.setText(R.string.str_pause); 
                  mStart.setImageResource(R.drawable.star);
                  mPause.setImageResource(R.drawable.pause_2);
                } 
                else if(bIsPaused==true) 
                { 
                  myPlayer1.start(); 
                  bIsPaused = false; 
                  mTextView1.setText(R.string.str_start);
                  mStart.setImageResource(R.drawable.stars);
                  mPause.setImageResource(R.drawable.pause);
                } 
              } 
            } 
          } 
        }); 
        
        mStop.setOnClickListener(new ImageButton.OnClickListener() 
        { 
          @Override 
          public void onClick(View v) 
          { 
            // TODO Auto-generated method stub 
            
            if(myPlayer1.isPlaying()==true) 
            { 
              myPlayer1.reset(); 
              mTextView1.setText(R.string.str_stopped);
              mStart.setImageResource(R.drawable.star);
              mPause.setImageResource(R.drawable.pause);
              mImageView1.setImageResource(R.drawable.black);
            } 
          } 
        }); 
    }
    
    private void setFile(String string) {
    	try { 	
    	
    	File f = new File(string);
		FileInputStream txtStream = new FileInputStream(f);
		in = new BufferedReader(new InputStreamReader(txtStream));
		String getLine;
		getLine = in.readLine();
		valArray = getLine.split(" ");
		
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
}

	private void setPlayer(String string) throws IllegalArgumentException, IllegalStateException, IOException {
    	myPlayer1.setDataSource( string );
		myPlayer1.prepare();
		mSeeker.setMax(myPlayer1.getDuration());
	
}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
    	String path;
		if( resultCode != 0){
			path = intent.getData().getPath();
			Pattern pattern = Pattern.compile("^(.+)\\/");
			Matcher matcher = pattern.matcher(path);
			matcher.find();
			String dir = matcher.group();
			setFile( dir +"/tagaff.txt"); 
			try {
				setPlayer(path);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//        data = new byte[2048];
//        
//        wavInfo = new WavInfo();
////        InputStream wavStream = getResources().openRawResource(R.raw.test);
//        InputStream wavStream;
//		try {
//			
//			//write file
//			File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//	        if (!root.exists()) {
//	            root.mkdirs();
//	        }
//	        File gpxfile = new File(root, "test.txt");
//	        
//			
//			
//			File f = new File(Environment.getExternalStorageDirectory()+"/test.wav");
//			wavStream = new FileInputStream(f);
//			BufferedInputStream bufferedInputStream = new BufferedInputStream(wavStream); 
//			try {
//				
//				FileWriter writer = new FileWriter(gpxfile);
//				
//				readHeader(wavStream);
////				data = readWavPcm(wavInfo,wavStream);
//				Log.e("start time",System.currentTimeMillis()+"");
//				writer.append("start time : " + System.currentTimeMillis()+"");
//				writer.flush();
//				while(bufferedInputStream.read(data) != -1) { 
////					Log.e("start mfcc time",System.currentTimeMillis()+"");
//					double[] doubleData = changeDataToDouble(data);
//					rmZero(doubleData);
//					// need downsampling to 22050
//					
//					 mfcc = extractMfcc(doubleData);
////					 Log.e("end mfcc time",System.currentTimeMillis()+"");
////					 mfcc.toString();
////					 Arrays.toString(mfcc);
//					 writer.append(Arrays.toString(mfcc));
//					 writer.flush();
//
//	            }
//				Log.e("end time",System.currentTimeMillis()+"");
//				writer.append("end time : " + System.currentTimeMillis()+"");
//				writer.flush();
//				writer.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.e("IO exception",System.currentTimeMillis()+"");
//			}
//			
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			Log.e("File exception",System.currentTimeMillis()+"");
//		}
//		Log.e("end",System.currentTimeMillis()+"");
//		
    
    
    
}