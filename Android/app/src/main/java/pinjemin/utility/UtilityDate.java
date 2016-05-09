/** ===================================================================================
 * [UTILITY DATE]
 * Helper class untuk melakukan formatting tanggal, etc.
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.utility;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pinjemin.R;


public class UtilityDate
{
	/** ==============================================================================
	 * Mengembalikkan nama bulan yang disingkat
	 * @param monthNum - nomor bulan (Jan = 0, Des = 11)
	 * @return string "Jan", "Feb", "Mar", ..., "Des"
	 * ============================================================================== */
	public static String monthNameAbbr(int monthNum) {
		switch (monthNum) {
			case 0: return "Jan";
			case 1: return "Feb";
			case 2: return "Mar";
			case 3: return "Apr";
			case 4: return "Mei";
			case 5: return "Jun";
			case 6: return "Jul";
			case 7: return "Agu";
			case 8: return "Sep";
			case 9: return "Okt";
			case 10: return "Nov";
			default: return "Des";
		}
	}

	/** ==============================================================================
	 * Menentukan apakah timelime perlu di-refresh kembali (mencegah terjadinya
	 * permintaan request berulang kali dalam jeda waktu yang singkat). Default
	 * refresh time: 5 menit.
	 * @param lastRequest - timestamp timeline terakhir kali di-refresh
	 * @return true jika timeline perlu di-refresh lagi
	 * ============================================================================== */
	public static boolean isToRefreshAgain(Calendar lastRequest) {
		if (lastRequest == null) return true;

		Calendar currentTime = Calendar.getInstance();

		// selisih waktu dalam ms
		long msDiff = currentTime.getTime().getTime() - lastRequest.getTime().getTime();

		// kalau sudah lebih dari 1 menit, return true
		if (msDiff >= 1000 * 60) {
			return true;
		}
		else {
			return false;
		}
	}

	/** ==============================================================================
	 * Memformat timestamp SQL menjadi bentuk: 1 menit / 2 jam / tanggal
	 * @param timestamp - bentuk timestamp sql, e.g. "2017-01-20 14:12:56"
	 * @return bentuk timestamp yang sudah di-format, e.g. "1 menit"
	 * ============================================================================== */
	public static String formatTimestampElapsedTime(String timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Calendar currentTime = Calendar.getInstance();
			Calendar parsedTime = new GregorianCalendar();
			parsedTime.setTime(format.parse(timestamp));

			// sudah ganti tahun
			long yearDiff = currentTime.get(Calendar.YEAR) - parsedTime.get(Calendar.YEAR);
			if (yearDiff >= 1) {
				return "" + parsedTime.get(Calendar.YEAR);
			}

			// lebih dari sehari
			long msDiff = currentTime.getTime().getTime() - parsedTime.getTime().getTime();
			if (msDiff >= 1000 * 60 * 60 * 24) {
				return "" + parsedTime.get(Calendar.DATE) + " "
					+ monthNameAbbr(parsedTime.get(Calendar.MONTH));
			}

			// lebih dari sejam
			if (msDiff >= 1000 * 60 * 60) {
				return "" + (msDiff / (1000 * 60 * 60)) + " jam";
			}

			// sisanya, kembalikan dalam menit
			long minDiff = msDiff / (1000 * 60);
			minDiff = Math.max(minDiff, 1);
			return "" + minDiff + " menit";
		}
		catch (ParseException e) {
			return "n/a";
		}
	}

	/** ==============================================================================
	 * Memformat timestamp SQL menjadi bentuk: 27 Des 2015
	 * @param timestamp - bentuk timestamp sql, e.g. "2017-01-20 14:12:56"
	 * @return bentuk timestamp yang sudah di-format, e.g. "27 Des 2015"
	 * ============================================================================== */
	public static String formatTimestampDateOnly(String timestamp) {
		String formattedDate = "";
		int year = Integer.parseInt(timestamp.substring(0, 4));
		int month = Integer.parseInt(timestamp.substring(5, 7));
		int date = Integer.parseInt(timestamp.substring(8, 10));

		// Dari database: indeks Januari = 1
		// Yang diterima monthNameAbbr: indeks Januari = 0
		// (jadi perlu di-decrement 1)
		formattedDate = date + " " + monthNameAbbr(month - 1) + " " + year;
		return formattedDate;
	}

	/** ==============================================================================
	 * Memformat timestamp SQL menjadi bentuk: 14:12
	 * @param timestamp - bentuk timestamp sql, e.g. "2017-01-20 14:12:56"
	 * @return bentuk timestamp yang sudah di-format, e.g. "14:12"
	 * ============================================================================== */
	public static String formatTimestampTimeOnly(String timestamp) {
		return timestamp.substring(11, 16);
	}
}
