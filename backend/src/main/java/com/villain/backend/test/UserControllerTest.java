package com.villain.backend.test;

import com.villain.backend.model.RoleType;
import com.villain.backend.model.User;
import com.villain.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> list() {
        return userRepository.findAll();
    }

    @DeleteMapping("/user/{id}")
    public String delete(@PathVariable int id) {
        try {
            userRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e) {
            return "삭제에 실패했습니다. " + id + "는 DB에 없습니다.";
        }
        return "삭제 되었습니다." + id;
    }

    @Transactional
    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User requestUser) {
        System.out.println("id: " + id);
        System.out.println("password: " + requestUser.getPassword());
        System.out.println("email: " + requestUser.getEmail());

        User user = userRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("수정에 실패했습니다.");
        });
        user.setPassword(requestUser.getPassword());
        user.setEmail(requestUser.getEmail());
        return user;
    }

    @GetMapping("/user")
    public List<User> pageList(@PageableDefault(size=2, sort="id", direction= Sort.Direction.DESC) Pageable pageable) {
        Page<User> pagingUser = userRepository.findAll(pageable);
        List<User> users = pagingUser.getContent();
        return users;
    }

    @GetMapping("/user/{id}")
    public User detail(@PathVariable int id) {
        User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("해당 유저는 없습니다. id : " + id);
            }
        });

        return user;
    }

    @PostMapping("/user/join")
    public String join(User user) {
        user.setRole(RoleType.USER);
        userRepository.save(user);
        return "회원가입이 완료되었습니다.";
    }
}
