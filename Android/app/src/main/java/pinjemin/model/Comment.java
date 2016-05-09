/** ===================================================================================
 * [COMMENT]
 * Kelas yang merepresentasikan instance sebuah comment
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.model;

public class Comment
{
	public static final int SYSTEM_NOTIFICATION_UID = -1;

	private String realName;
	private String timestamp;
	private String content;
	private int cid;
	private int uid;


	/** ==============================================================================
	 * Constructor kelas Comment
	 * ============================================================================== */
	public Comment(String realName, String timestamp, String content, int cid, int makerUid) {
		this.cid = cid;
		this.content = content;
		this.realName = realName;
		this.timestamp = timestamp;
		this.uid = makerUid;
	}


	// --- setter & getter methods ---

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
