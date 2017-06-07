/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gacs.common.constants;

public class GeneralConstants {

	
	/**
	 * Reg.expression used to validate target names. 
	 * Starts with a letter or number, then it can contain any word character (\w), a space, a dot, a comma, 
	 * a minus or a plus sign, and ends with a word character (\w). Note that \w is short for [a-zA-Z_0-9]
	 * 
	 * Example of target names:
	 *  2MASS J00420949+4117456
	 *  MCG+07-02-012
	 *	m 31
	 *  Cl* Trumpler 14 MJ 251
	 *  3c218
	 *
	 */
	public static final String TARGET_NAME_VALID_PATTERN = "[a-zA-Z0-9][-+\\* .\\w]*[\\w]";


	/**
	 * Observation ID length
	 */
	public static final int OBSERVATION_ID_LENGTH = 10;
	
	/**
	 * Default Field Of View radius for searching by position (in arc min)
	 */
	public static final int DEFAULT_FOV_RADIUS_ARC_MIN = 15;
	
	/**
	 * Default Field Of View radius for searching by position the Slew pseudo-exposures 
	 * (in arc min)
	 */
	public static final int DEFAULT_FOV_RADIUS_ARC_MIN_SLEW_EXPOSURES = 30;
	
	

	/**
	 * Encryption key used for encrypting data in client-server requests (and backwards)
	 * 
	 * Important!!! If this key is changed, also the one in the class CryptoUtils.java 
	 * in the Web client project must be changed to be the same  
	 */
	public static final byte[] NXSA_TRIPLE_DES_CIPHER_KEY = new byte[] { (byte) 3, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1,
		(byte) 1, (byte) 1, (byte) 4, (byte) 1, (byte) 1, (byte) 8, (byte) 1, (byte) 1, (byte) 1, (byte) 1,
		(byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 3, };

	
	
	
	/////////////////////////////////////////////////////////////////////////
	//   Patterns used to validate RA/DEC format
	/////////////////////////////////////////////////////////////////////////

	private static final String BLANK = "\\s*";

	// private static final String SEP = "[\\s\\,]+";

	private static final String SIGN = "([+-])?";

	private static final String BEGIN = "^" + BLANK;

	private static final String END = BLANK + "$";

	// Units
	/*
	 * public static final String UNIT_DEGREE = "^d$";
	 * 
	 * public static final String UNIT_AMIN = "^(m|\\')$"; // arc minutes
	 * 
	 * public static final String UNIT_ASEC = "^(s|\\'{2})$"; // arc seconds
	 */
	// Atomic elements
	private static final String HOUR = "([0-2]?[0-9])";

	private static final String MIN = "([0-5]?[0-9])";

	private static final String SEC = "([0-5]?[0-9]\\.?\\d*)";

	private static final String SEP2 = "[\\s\\:\\,]+";

	private static final String DEG = "(\\d{1,2})";

	private static final String DEG_LONG = "(\\d{1,3})";

	private static final String RADII = "[\\s\\,]*" + "(?:(\\d+\\.?\\d*)(d|m|s|\\'{1,2})?)?" + BLANK;

	private static final String HOUR_UNIT = "(?:([0-2]?[0-9])h)?";

	private static final String MIN_UNIT = "(?:([0-5]?[0-9])m)?";

	private static final String SEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)s)?";

	private static final String DEG_UNIT = "(?:(\\d{1,2})d)?";

	private static final String DEG_LONG_UNIT = "(?:(\\d{1,3})d)?";

	private static final String AMIN_UNIT = "(?:([0-5]?[0-9])[m\\'])?";

	private static final String ASEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)(?:s|\\\"))?";

	// RA format: 1, 2 or 3 numbers without units or any with them
	public static final String RA_DECIMAL_PATTERN = BEGIN + "([\\d\\.]+)([dh])?" + END;

	public static final String RA_TWO_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + END;

	public static final String RA_THREE_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + SEP2 + SEC + END;

	public static final String RA_UNITS_PATTERN = BEGIN + HOUR_UNIT + BLANK + MIN_UNIT + BLANK + SEC_UNIT + END;

	// DEC format: 1, 2 or 3 numbers without units or any with them
	public static final String DEC_DECIMAL_PATTERN = BEGIN + SIGN + BLANK + "([\\d\\.]+)d?" + END;

	public static final String DEC_TWO_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN + END;

	public static final String DEC_THREE_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN + SEP2 + SEC + END;

	public static final String DEC_UNITS_PATTERN = BEGIN + SIGN + DEG_UNIT + BLANK + AMIN_UNIT + BLANK + ASEC_UNIT
			+ END;
	
	
   // LONGITUD format: 1, 2 or 3 numbers without units or any with them
   public static final String LONGITUD_DECIMAL_PATTERN = BEGIN + "([\\d\\.]+)d?" + END;

   public static final String LONGITUD_TWO_NUMBERS_PATTERN = BEGIN + DEG_LONG + SEP2 + MIN + END;

   public static final String LONGITUD_THREE_NUMBERS_PATTERN = BEGIN + DEG_LONG + SEP2 + MIN
         + SEP2 + SEC + END;

   public static final String LONGITUD_UNITS_PATTERN = BEGIN + DEG_LONG_UNIT + BLANK + AMIN_UNIT
         + BLANK + ASEC_UNIT + END;
	
	// User fields format
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
	public static final String PHONE_PATTERN = "";
		
	public static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,8}$";
		
	/**
	 * Password pattern:
	 *  (				# Start of group
	 *  (?=.*[a-zA-z])	# lowercase or uppercase letters
  	 *  (?=.*\d)		# must contains at least one digit from 0-9
  	 *  (?=[\\S]+$)		# no white spaces along the string
     *  .				# match anything with previous condition checking
     *  {6,20}			# length at least 6 characters and maximum of 20	
     *  )				# End of group
	 */
	public static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-z])(?=[\\S]+$).{6,20})";

	
	
	// AUTHENTICATION MECHANISM FOR TAP JOBS
	public static final String JOBOWNER_ANONYMOUS_ID = "anonymous";

}

