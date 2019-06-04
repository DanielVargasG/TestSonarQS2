package edn.cloud.business.dto.odata;

public class UserManager {

	private static final ThreadLocal<String> userId = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> isUserAdmin = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> isUserSuperAdmin = new ThreadLocal<>();
	private static final ThreadLocal<String> countManaged = new ThreadLocal<>();
	private static final ThreadLocal<String> countHred = new ThreadLocal<>();

	public static void setUserId(String data) {
		userId.set(data);
	}

	public static String getUserId() {
		return userId.get();
	}

	public static void setIsUserAdmin(Boolean data) {
		isUserAdmin.set(data);
	}
	
	public static void setIsUserSuperAdmin(Boolean data) {
		isUserSuperAdmin.set(data);
	}

	public static Boolean getIsUserAdmin() {
		return isUserAdmin.get();
	}
	
	public static Boolean getIsUserSuperAdmin() {
		return isUserSuperAdmin.get();
	}

	public static void setCountManaged(String data) {
		countManaged.set(data);
	}
	
	public static String getCountManaged() {
		return countManaged.get();
	}
	
	public static void setCountHred(String data) {
		countHred.set(data);
	}
	
	public static String getCountHred() {
		return countHred.get();
	}
	
	public static void cleanUp() {
		userId.remove();
		isUserAdmin.remove();
		isUserSuperAdmin.remove();
		countHred.remove();
		countManaged.remove();
	}

}
