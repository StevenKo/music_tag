package sinica.music.tag.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import sinica.music.tag.mfcc.MFCC;
import sinica.music.tag.project.Music_tagActivity.WavInfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class Music_tagActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String RIFF_HEADER = "RIFF";
	private static final String WAVE_HEADER = "WAVE";
	private static final String FMT_HEADER = "fmt ";
	private static final String DATA_HEADER = "data";
    private static final int HEADER_SIZE = 44;
	private static final String CHARSET = "ASCII";
	private static WavInfo wavInfo;
	byte[] data;
	 
	class WavInfo{
		public int format;
		public int channels;
		public int rate;
		public int bits;
		public int dataSize;
	}
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        wavInfo = new WavInfo();
        InputStream wavStream = getResources().openRawResource(R.raw.choppersound);
        
        
        try {
			readHeader(wavStream);
			data = readWavPcm(wavInfo,wavStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        double[] doubleData = changeDataToDouble(data);
   
        
        int nnumberOfMFCCParameters = 22;
		int nlifteringCoefficient = 12;
		ArrayList<Double> mfcc = extractMfcc(doubleData, nnumberOfMFCCParameters, nlifteringCoefficient);
		
		
        
    }
    
    private double[] changeDataToDouble(byte[] data) {
    	
    	int bytes = wavInfo.bits /8 ;
    	double [] doubleData = new double[wavInfo.dataSize / bytes];
    	
    	if( bytes == 2){
    		for(int i = 0 ; i < doubleData.length / bytes -1 ; i++ ){
    			doubleData[i] = (double)((data [(i)*2] & 0xff) | (data[(i)*2 + 1] << 8));
    		}
    	}else if( bytes == 4){
    		for(int i = 0 ; i < doubleData.length / bytes -1 ; i++ ){
    			doubleData[i] = (double)((data [(i)*4] & 0xff) | (data[(i)*4 + 1] << 8) | (data[(i)*4 + 2] << 16) | (data[(i)*4 + 3] << 24));
    		}
    	}
    	
    	
    	return doubleData;
	}

	public static byte[] readWavPcm(WavInfo info, InputStream stream) throws IOException {
    	  byte[] data = new byte[info.dataSize];
    	  stream.read(data, 0, data.length);
    	  return data;
    }
    
    private ArrayList<Double> extractMfcc(double[] doubleData, int a, int b) {
    	 int nnumberofFilters = 24;	
         int nlifteringCoefficient = b;	//earlier value was 22, now set to a-20
         boolean oisLifteringEnabled = true;
         boolean oisZeroThCepstralCoefficientCalculated = false;
         int nnumberOfMFCCParameters = a; //earlier value was 12, now set to a-10//without considering 0-th
         double dsamplingFrequency = 8000.0;
         int nFFTLength = 512;
         ArrayList<Double> mfcc_parameters = new ArrayList<Double>();
         
         if (oisZeroThCepstralCoefficientCalculated) {
             //take in account the zero-th MFCC
             nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1;
         }else {
             nnumberOfMFCCParameters = nnumberOfMFCCParameters;
         }
         
         MFCC mfcc = new MFCC(nnumberOfMFCCParameters,
                 dsamplingFrequency,
                 nnumberofFilters,
                 nFFTLength,
                 oisLifteringEnabled,
                 nlifteringCoefficient,
                 oisZeroThCepstralCoefficientCalculated);
        
//  need to check
         
         for (int i =0 ; i < doubleData.length / nFFTLength ; i ++)
         {
        	 try{
                double[] test = Arrays.copyOfRange(doubleData, i*512, i*512 + 511);
                double[] dparameters = mfcc.getParameters(test);
                for (int j = 0; j < dparameters.length; j++) 
                {
                	mfcc_parameters.add(dparameters[j]);
                }
        	 }catch(Exception e){
        		 
        	 }
         }
         return mfcc_parameters;
         

	}

	public static void readHeader(InputStream wavStream)
    		throws IOException {
    		 
    		    ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
    		    buffer.order(ByteOrder.LITTLE_ENDIAN);
    		 
    		    wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
    		 
    		    buffer.rewind();
    		    buffer.position(buffer.position() + 20);
    		    int format = buffer.getShort();
//    		    checkFormat(format == 1, "Unsupported encoding: " + format); // 1 means Linear PCM
    		    Log.i( "Encoding : 1 means Linear PCM : ", format + "");
    		    wavInfo.format = format;
    		    
    		    
    		    int channels = buffer.getShort();
//    		    checkFormat(channels == 1 || channels == 2, "Unsupported channels: " + channels);
    		    Log.i( "Channels : should be 1 or 2: ", channels + "");
    		    wavInfo.channels = channels;
    		    
    		    int rate = buffer.getInt();
//    		    checkFormat(rate <= 48000 && rate >= 11025, "Unsupported rate: " + rate);
    		    Log.i( "Sampling rate : ", rate + "");
    		    wavInfo.rate = rate;
    		    
    		    buffer.position(buffer.position() + 6);
    		    int bits = buffer.getShort();
//    		    checkFormat(bits == 16, "Unsupported bits: " + bits);
    		    Log.i( "Sampling size : ", bits + "");
    		    wavInfo.bits = bits;
    		    
    		    int dataSize = 0;
    		    while (buffer.getInt() != 0x61746164) { // "data" marker
    		      Log.i( "TAG", "Skipping non-data chunk");
    		      int size = buffer.getInt();
    		      wavStream.skip(size);
    		 
    		      buffer.rewind();
    		      wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
    		      buffer.rewind();
    		    }
    		    dataSize = buffer.getInt();
    		    Log.i( "Data size : ", dataSize + "");
    		    wavInfo.dataSize = dataSize;
    		    
    		    
//    		    checkFormat(dataSize > 0, "wrong datasize: " + dataSize);
    		 
//    		    return new WavInfo(new FormatSpec(rate, channels == 2), dataSize);
    		  }
}