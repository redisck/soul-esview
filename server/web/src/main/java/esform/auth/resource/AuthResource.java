package esform.auth.resource;

import esform.auth.request.AuthRequest;
import esform.dao.UserDao;
import esform.domain.User;
import esform.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by
 *
 * @name:孙证杰
 * @email:20076581@qq.com on 2017/9/17.
 */
@Controller
@RequestMapping("auth")
public class AuthResource {

    @Autowired
    private UserDao userDao;

    @PostMapping("register")
    @ResponseBody
    @Transactional
    public Response register(@RequestBody AuthRequest request, HttpServletResponse response) {

        User user = request.getDomain();

        User select = userDao.select(new User(user.getUserName()));
        if (null != select) {
            return Response.unAuthenticated("username already registered");
        }

        userDao.add(user);

        addAccessToken(response,user);

        return Response.ok();
    }

    @PostMapping("login")
    @ResponseBody
    public Response login(@RequestBody AuthRequest request, HttpServletResponse response) {

        User user = request.getDomain();

        User select = userDao.select(user);
        if (null == select) {
            return Response.unAuthenticated("username not exists");
        }

        if (!StringUtils.equals(select.getPassword(), user.getPassword())) {
            return Response.unAuthenticated("username or password wrong");
        }

        addAccessToken(response,user);

        return Response.ok();
    }

    @GetMapping("logout")
    @ResponseBody
    public Response clearAccessToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Response.ok();
    }

    private void addAccessToken(HttpServletResponse response,User user){
        Cookie cookie = new Cookie("access_token", user.getUserName() + "@" + user.getPassword());
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}

