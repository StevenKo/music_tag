package sinica.music.tag.project;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import sinica.music.tag.mfcc.MFCC;
//import sinica.music.tag.project.WavInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Music_tagActivity extends Activity {
    
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
    double[][] val;
    private BufferedReader in;
	private TextView mTag1;
	Spinner spinner;
    
    double[] mfcc;
    ArrayList<TextView> tags = new ArrayList<TextView>();;
	
	Runnable start=new Runnable(){
		 
		@Override
		public void run() {
			try 
            {   
              if(myPlayer1.isPlaying()==true) 
              {
                myPlayer1.reset();
                myPlayer1.prepare();
              }
              myPlayer1.start(); 
              mTextView1.setText(R.string.str_start); 
            } 
            catch (IllegalStateException e) 
            { 
              mTextView1.setText(e.toString()); 
              e.printStackTrace(); 
            } 
            catch (IOException e) 
            { 
              mTextView1.setText(e.toString()); 
              e.printStackTrace(); 
            } 
             
            myPlayer1.setOnCompletionListener(new OnCompletionListener() 
            { 
              public void onCompletion(MediaPlayer arg0) 
              {  
                mTextView1.setText(R.string.str_finished);
                mStart.setImageResource(R.drawable.star);
              } 
            });
			handler.post(updatesb);
			handler.postDelayed(updateTag, 500);
		}
 
    };
    
    Runnable updateTag = new Runnable(){

		@Override
		public void run() {
			handler.postDelayed(updateTag, 100);
			int currentTime = myPlayer1.getCurrentPosition();
			for( int tag_i = 0; tag_i < 1 ; tag_i++)
			{
			  setTagValue(currentTime, tag_i);
			}
//			String getLine;
//			try {
//				getLine = in.readLine();
//				valArray = getLine.split("\\s+");
//				valDoubleArray = new double[valArray.length-1];
//				for(int i=0; i<valDoubleArray.length;i++){
//					valDoubleArray[i] = Double.parseDouble(valArray[i+1]);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			mTag1.setTextSize((float) ((float) Math.exp(valDoubleArray[0])* 15.0));
//			Double.parseDouble(valArray[1]);
		}

		private void setTagValue(int currentTime, int tag_i) {
			int x = currentTime/25;
            double sum = val[x][tag_i];
			for(int i=0; i < 4; i++){
				sum += val[x+i][tag_i];
				sum += val[x-i][tag_i];
            }
			tags.get(tag_i).setTextSize((float) ((float) Math.exp(sum/9)* 15.0));
		}
    	
    };
    
    Runnable updatesb =new Runnable(){
    	 
		@Override
		public void run() {
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
        spinner = (Spinner) findViewById(R.id.spinnner);
        setSpinner();
        setClickListener();
        
        tags.add(mTag1);
        
    }
    
    private void setSpinner() {
    	ArrayAdapter<Double> adapter = new ArrayAdapter<Double>(this,android.R.layout.simple_spinner_item,new Double[]{ 0.125,0.25});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
	
   }

	private void setClickListener() {
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
         	  handler.post(start);
           } 
         });     
          
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
         
         spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
             public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                 Toast.makeText(Music_tagActivity.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
             }
             public void onNothingSelected(AdapterView arg0) {
                 Toast.makeText(Music_tagActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
             }
         });
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
			readFile(dir);
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

	private void setFile(String string) {
    	try { 	
    	
    	File f = new File(string);
		FileInputStream txtStream = new FileInputStream(f);
		in = new BufferedReader(new InputStreamReader(txtStream));
		String getLine;
		getLine = in.readLine();
		ArrayList<double[]> data = new ArrayList<double[]>();
		while((getLine = in.readLine()) != null){
			String[] valPosArray = getLine.split("\\s+");
			double[] valDoubleArray = new double[valPosArray.length-1];
			for(int i=0; i<valDoubleArray.length;i++){
				valDoubleArray[i] = Double.parseDouble(valPosArray[i+1]);
			}
			data.add(valDoubleArray);
			
		}
		
		val = new double[data.size()][45];
		data.toArray(val);
		
    	} catch (IOException e) {
			e.printStackTrace();
		}
	
}
	private void readFile(String dir) {
//		try { 	
//	    	File f = new File(dir + "posterior.txt");
//			FileInputStream txtStream = new FileInputStream(f);
//			BufferedReader inPosteriior = new BufferedReader(new InputStreamReader(txtStream));
//			String getLine;
//			ArrayList<double[]> data = new ArrayList<double[]>();
//			while((getLine = inPosteriior.readLine()) != null){
//				String[] valPosArray = getLine.split("\\s+");
//				double[] valDoubleArray = new double[valPosArray.length-1];
//				for(int i=0; i<valDoubleArray.length;i++){
//					valDoubleArray[i] = Double.parseDouble(valPosArray[i+1]);
//				}
//				data.add(valDoubleArray);
//			}
//
//			//getLine = "";
//	    } catch (IOException e) {
//				e.printStackTrace();
//		}
		
	}
    
    
    
}