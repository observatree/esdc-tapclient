import nom.tam.fits.*;
import java.io.*;
import java.util.StringTokenizer;
import java.lang.reflect.Array;

public class CreateHealpix {

	public static void main(String args[]){
	
		String templateFile 	= "./gacs_template.fits";
		String outputFileString	= "./output.fits";
		String arrayFile	= "./array_file.txt";
	
		if (args.length > 0) {
			for(int i=0; i < args.length; i++) {
				if(args[i].equals("-input")) arrayFile = args[i+1];
				if(args[i].equals("-output")) outputFileString = args[i+1];	
			}
		} else {
			System.out.println("Usage: java -cp .:fits.jar CreateHealpix -input <input_Array> [-output <Output Healpix Map>]");
			return;
		}
		

			
		try {
		
		//It takes a template file to be modified with the output content
			Fits 		templateHealpix = new Fits(templateFile);
			Fits 		outputFits 	= new Fits();
			
		//It will generate the output.fits file in return	
			File 		outputFile 	= new File(outputFileString);
			outputFile.createNewFile();
			
			DataOutputStream odata = new DataOutputStream(new FileOutputStream(outputFile));
						
			BasicHDU primaryHDU 	= templateHealpix.getHDU(0);
			outputFits.addHDU(primaryHDU);
			
			
		//It reads as first argument the input file where the density map info in contained
		//it is an ASCII table with a first title line and the rest separated by ";"
		//First column is the ipix and the second is the measured value
			 
			BufferedReader 	br 	= new BufferedReader(new FileReader(arrayFile));
       			StringBuilder 	sb 	= new StringBuilder();
        		String 		line 	= br.readLine();
			line = br.readLine();
			
			BinaryTableHDU 	binaryTableHDU 	= (BinaryTableHDU) templateHealpix.getHDU(1);
			
			StringTokenizer st	= null;
			
			float[] floatArray = new float[3145728];
			int k = 0;
			while(line != null && k < 3145728) {
				
				st = new StringTokenizer(line, ";"); 
				
				int 	ipix 	= (new Integer(st.nextToken())).intValue();
				float 	density	= (new Float(st.nextToken())).floatValue();


				//In case there are gaps (no value for certain ipix indexes) a value 0 is added
				//so we have a complete set as input for the healpix map	
				boolean addingZeroes = false;
				while(ipix > k && k < 3145728) {
				
					 addingZeroes = true;
					floatArray[k] = 0;					
					k++;
				}	
				if(addingZeroes) k--;
				floatArray[k] = density;
				line = br.readLine();
				k++;
			} 
			
			
			// We modify the array by the generated float Array			
			binaryTableHDU.setColumn(0,floatArray);
			outputFits.addHDU(binaryTableHDU);		

			//Write content in output file			
			outputFits.write(odata);		
	
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
}
