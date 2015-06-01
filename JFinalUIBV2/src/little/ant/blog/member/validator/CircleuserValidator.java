package little.ant.blog.member.validator;

import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import little.ant.blog.model.Circleuser;

public class CircleuserValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(CircleuserValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/jf/blog/member/circleuser/save")){
			// validateString("username", 6, 30, "usernameMsg", "请输入登录账号!");
			
		} else if (actionKey.equals("/jf/blog/member/circleuser/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Circleuser.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/jf/blog/member/circleuser/save")){
			controller.render("/blog/member/xxx.html");
		
		} else if (actionKey.equals("/jf/blog/member/circleuser/update")){
			controller.render("/blog/member/xxx.html");
		
		}
	}
	
}
