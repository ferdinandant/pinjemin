/** ===================================================================================
 * [RECYCLER ON ITEM TOUCH LISTENER]
 * Listener untuk RecycleView di TimelineDemandFragment dan TimelineSupplyFragment
 * Memanggil
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */


package pinjemin.behavior;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import pinjemin.behavior.ClickListener;


/** ==============================================================================
 * Custom implementation kelas RecyclerView.OnItemTouchListener, digunakan untuk
 * mengatur behavior saat ada item di recycler view (suatu post item) ditekan
 * ============================================================================== */
public class RecyclerOnItemTouchListener implements RecyclerView.OnItemTouchListener
{
	private GestureDetector gestureDetector;
	private ClickListener clickListener;

	/** ==============================================================================
	 * Constructor kelas RecyclerOnItemTouchListener
	 * @param context - context dari mana kelas ini dipanggil
	 * @param recyclerView - RecyclerView yang di-listen
	 * @param clickListener - instance ClickListener yang akan meng-handle touch events
	 *   (e.g. ClickListener yang bertugas memulai activity lihat detail post untuk
	 *   post yang ditekan.)
	 * ============================================================================== */
	public RecyclerOnItemTouchListener(Context context,
		final RecyclerView recyclerView, final ClickListener clickListener
	) {
		// initialize clickListener dan gestureDetector:
		// ClickListener nanti dipakai untuk
		this.clickListener = clickListener;

		// GestureDetector berfungsi untuk mendeteksi setiap macam MotionEvent
		// (e.g. jenis swipe, pressure-nya berapa, posisi pointer)
		this.gestureDetector = new GestureDetector(context,
			new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
					if (child != null && clickListener != null) {
						clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
					}
				}
			});
	}

	/** ==============================================================================
	 * Untuk meng-observe touch event sebelum dia di-handle oleh RecyclerView atau
	 * view anak-anaknya.
	 * ============================================================================== */
	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		// brdasarkan posisi klik, ambil reference view object yang dipilih
		View child = rv.findChildViewUnder(e.getX(), e.getY());

		// jika syarat-syarat yang tidak menyebabkan error terpenuhi,
		// jalankan handler pada clickListener yang diberikan pada constructor
		if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
			clickListener.onClick(child, rv.getChildAdapterPosition(child));
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}