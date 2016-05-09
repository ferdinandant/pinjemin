/** ===================================================================================
 * [CUSTOM COMMENT BLOCK]
 * Kelas yang berfungsi untuk membuat view thread comment pada halaman detail post.
 * (satu thread comment mengandung beberapa entri comment, ditambah dengan action
 * buttons di bawahnya)
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.comment;


import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import pinjemin.model.Comment;
import pinjemin.utility.UtilityGUI;


public class CustomThreadBlock
{
	public static final int ACTIONS_NONE = 0;
	public static final int ACTIONS_CAN_INITIATE = 1;
	public static final int ACTIONS_CAN_CONFIRM = 2;
	public static final int ACTIONS_CAN_CANCEL = 3;

	public static final int DEMAND_POST = 0;
	public static final int SUPPLY_POST = 1;
	public static final int PEMINJAMAN_POST = 2;

	public static final int COLOR_BLACK = 0xff000000;
	public static final int COLOR_WHITE = 0xffffffff;
	public static final int COLOR_GRAY = 0xffcecece;
	public static final int COLOR_TRANSPARENT = 0x00ffffff;

	private LinearLayout commentThreadViewContainer;
	private ArrayList<Comment> commentEntries;
	private Activity activity;
	private int parentUID;
	private int postPID;
	private int possibleAction;


	/** ==============================================================================
	 * Constructor kelas CustomThreadBlock
	 * ============================================================================== */
	public CustomThreadBlock(Activity activity, ArrayList<Comment> commentEntries,
		int postPID, int parentUID, int possibleAction
	) {
		this.activity = activity;
		this.commentEntries = commentEntries;
		this.postPID = postPID;
		this.parentUID = parentUID;
		this.possibleAction = possibleAction;

		// attach semua data ke dalam satu LinearLayout
		constructLinearLayout();
	}


	/** ==============================================================================
	 * Untuk meng-instantiate LinearLayout commentThreadViewContainer dan meng-attach
	 * semua child view ke dalam commentThreadViewContainer.
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
		commentThreadViewContainer = new LinearLayout(activity);
		commentThreadViewContainer.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
		commentThreadViewContainer.setPadding(0, 0, 0, 0);
		commentThreadViewContainer.setOrientation(LinearLayout.VERTICAL);
		commentThreadViewContainer.setBackgroundColor(COLOR_TRANSPARENT);

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

		// configure action buttons
		CustomCommentActionBlock actionButtonBlock = new CustomCommentActionBlock(
			activity, postPID, parentUID, possibleAction);

		// configure gap
		// (untuk jarak dengan thread lainnya)
		View gap = new View(activity);
		gap.setLayoutParams(new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, padding_10dp));
		gap.setBackgroundColor(COLOR_TRANSPARENT);


		// --- ATTACH SEGALA SESUATU ---

		// attach upper border
		commentThreadViewContainer.addView(upperBorder);

		// untuk setiap comment di commentEntries
		// buatkan masing-masing CustomCommentBlock-nya, lalu attach
		for (Comment comment : commentEntries) {
			CustomCommentBlock commentBlock = new CustomCommentBlock(activity, comment);
			commentThreadViewContainer.addView(commentBlock.getLinearLayout());
		}

		//attach action buttons
		commentThreadViewContainer.addView(actionButtonBlock.getLinearLayout());

		// attach bottom border
		commentThreadViewContainer.addView(bottomBorder);

		// attach gap
		commentThreadViewContainer.addView(gap);
	}

	/** ==============================================================================
	 * Mengembalikan instance LinearLayout yang mengandung data satu thread komentar,
	 * termasuk action buttons-nya.
	 * @return instance LinearLayout, siap untuk di-attach ke parent view-nya.
	 * ============================================================================== */
	public LinearLayout getLinearLayout() {
		return commentThreadViewContainer;
	}
}
