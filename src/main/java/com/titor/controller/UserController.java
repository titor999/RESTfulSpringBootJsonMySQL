package com.titor.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.titor.constant.Constant;
import com.titor.entity.User;
import com.titor.services.UserServices;

@Controller
@RequestMapping(value = Constant.USER_CONTROLLER_REQUEST)
@Api("User controller annotation")
public class UserController {
	@Autowired
	private UserServices userService;

	@ApiOperation("Home request")
	@GetMapping(value = Constant.HOME_REQUEST)
	private String home() {
		return Constant.HOME;
	}

	@ApiOperation("Register user request")
	@GetMapping(value = Constant.REGISTER_USER_REQUEST)
	private String registerUser(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return Constant.REGISTER;
	}

	@ApiOperation("Save user request")
	@PostMapping(value = Constant.SAVE_USER_REQUEST)
	private String AddUser(@ModelAttribute("user") User user, @RequestPart("file") MultipartFile[] file, Model model) {
		Path path = null;
		String pathh = null;
		for (MultipartFile file2 : file) {
			pathh = Constant.UPLOAD_FILE_DIRECTORY + file2.getOriginalFilename();
			path = Paths.get(pathh);
			try {
				Files.write(path, file2.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		user.setProfile(pathh);
		userService.insert(user);
		return Constant.REDIRECT_USER_LIST_REQUEST;
	}

	private char[] getFileExtension(File rfile) {
		// TODO Auto-generated method stub
		return null;
	}

	@ApiOperation("Get all user request")
	@GetMapping(value = Constant.GET_ALL_USER_REQUEST)
	private String getAllUser(Model model) throws IOException {
		List<User> list = userService.getAllUser();
		String imgString = null;
		model.addAttribute("list", list);
		for (User u : list) {
			File file = new File(u.getProfile().toString());
			byte[] byteImage = Files.readAllBytes(file.toPath());
			imgString = Base64.encodeBase64String(byteImage);
			u.setProfile(imgString);
		}
		return Constant.ALL_USER_LIST;
	}

	@ApiOperation("Update user request")
	@GetMapping(value = Constant.UPDATE_USER_REQUEST)
	private String getuserById(@RequestParam("id") int id, Model model) throws IOException {
		User user = userService.findUser(id);
		File file = new File(user.getProfile().toString());
		byte[] byteImage = Files.readAllBytes(file.toPath());
		String imgString = Base64.encodeBase64String(byteImage);
		model.addAttribute("imgString", imgString);
		model.addAttribute("user", user);
		return Constant.UPDATE_USER;
	}

	@ApiOperation("Delete user request")
	@GetMapping(value = Constant.DELETE_USER_REQUEST)
	private String delete(@PathVariable("id") int id) {
		System.out.println(id);
		User user = userService.findUser(id);
		File file = new File(user.getProfile().toString());
		boolean b = file.delete();
		System.out.println(b);
		userService.delete(id);
		
		return Constant.REDIRECT_USER_LIST_REQUEST;
	}
}
