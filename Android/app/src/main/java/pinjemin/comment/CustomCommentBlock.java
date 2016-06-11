/** ===================================================================================
 * [CUSTOM COMMENT BLOCK]
 * Kelas yang berfungsi untuk membuat view entri comment pada halaman detail post.
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.comment;


import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pinjemin.model.Comment;
import pinjemin.utility.UtilityDate;
import pinjemin.utility.UtilityGUI;


public class CustomCommentBlock
{
	public static final int COLOR_BLACK = 0x88000000;
	public static final int COLOR_RED = 0x88880000;
	public static final int COLOR_WHITE = 0x88ffffff;
	public static final int COLOR_GRAY = 0xffcecece;

	private LinearLayout commentEntryViewContainer;
	private Activity activity;
	private Comment comment;


	/** ==============================================================================
	 * Constructor kelas CustomCommentBlock
	 * ============================================================================== */
	public CustomCommentBlock(Activity activity, Comment comment) {
		this.activity = activity;
		this.comment = comment;

		// attach semua data ke dalam satu LinearLayout
		constructLinearLayout();
	}

	/** ==============================================================================
	 * Untuk meng-instantiate LinearLayout commentEntryViewContainer dan meng-attach
	 * semua child view ke dalam commentEntryViewContainer.
	 * ============================================================================== */
	private void constructLinearLayout() {
		// menghitung konversi satuan dari dp ke px
		int padding_10dp = UtilityGUI.dpIntoPixel(activity, 10);
		int padding_5dp = UtilityGUI.dpIntoPixel(activity, 5);

		// bentuk linear layout baru
		// setLayoutParams: width=MATCH_PARENT, height=WRAP_CONTENT
		// setOrientation: set orientasi vertikal
		// setPadding: set padding dalam px (left, top, right, bottom)
		// setBackgroundColor: set backgroundc color dalam hex (0xAARRGGBB)
		commentEntryViewContainer = new LinearLayout(activity);
		commentEntryViewContainer.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
		commentEntryViewContainer.setPadding(0, 0, 0, 0);
		commentEntryViewContainer.setOrientation(LinearLayout.VERTICAL);
		commentEntryViewContainer.setBackgroundColor(COLOR_WHITE);

		// configure textview untuk nama pengarang komentar
		// special handling kalau dia ternyata system notification
		TextView commentAuthor = new TextView(activity);
		commentAuthor.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
		commentAuthor.setTypeface(null, Typeface.BOLD);
		commentAuthor.setPadding(padding_10dp, padding_10dp, padding_10dp, 0);

		if (comment.getUid() == Comment.SYSTEM_NOTIFICATION_UID) {
			commentAuthor.setText("[NOTIFIKASI SISTEM]");
			commentAuthor.setTextColor(COLOR_RED);
		}
		else {
			commentAuthor.setText(comment.getRealName());
			commentAuthor.setTextColor(COLOR_BLACK);
		}

		// configure textview untuk timestamp komentar
		// set sedikit bottom padding di bawah ini
		TextView commentTimestamp = new TextView(activity);
		commentTimestamp.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
		commentTimestamp.setText("Diposkan pada "
			+ UtilityDate.formatTimestampDateOnly(comment.getTimestamp())
			+ ", jam " + UtilityDate.formatTimestampTimeOnly(comment.getTimestamp()));
		commentTimestamp.setTextColor(COLOR_BLACK);
		commentTimestamp.setPadding(padding_10dp, 0, padding_10dp, padding_5dp);

		// configure textview untuk isi komentar
		TextView commentContent = new TextView(activity);
		commentContent.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
		commentContent.setText(comment.getContent());
		commentContent.setTextColor(COLOR_BLACK);
		commentContent.setPadding(padding_10dp, 0, padding_10dp, padding_10dp);


		// configure garis border atas dan bawah
		// setLayoutParams: width=MATCH_PARENT, height=1px
		View upperBorder = new View(activity);
		upperBorder.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 1));
		upperBorder.setBackgroundColor(COLOR_GRAY);

		View bottomBorder = new View(activity);
		bottomBorder.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 1));
		bottomBorder.setBackgroundColor(COLOR_GRAY);

		// attach semua ke commentEntryViewContainer
		commentEntryViewContainer.addView(upperBorder);
		commentEntryViewContainer.addView(commentAuthor);
		commentEntryViewContainer.addView(commentTimestamp);
		commentEntryViewContainer.addView(commentContent);
		commentEntryViewContainer.addView(bottomBorder);
	}

	/** ==============================================================================
	 * Mengembalikan instance LinearLayout yang mengandung data satu entri komentar
	 * @return instance LinearLayout, siap untuk di-attach ke parent view-nya.
	 * ============================================================================== */
	public LinearLayout getLinearLayout() {
		return commentEntryViewContainer;
	}


}
