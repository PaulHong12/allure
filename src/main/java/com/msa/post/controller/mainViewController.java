package com.msa.post.controller;

import com.msa.member.domain.Member;
import com.msa.member.service.MemberService;
import com.msa.post.domain.Post;
import com.msa.post.dto.PostDto;
import com.msa.post.service.PostService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
public class mainViewController {

    // Assuming you have a service to handle post retrieval
    private final PostService postService;
    private final MemberService memberService;

    // Constructor for controller
    public mainViewController(PostService postService, MemberService memberService, PostController postController) {
        this.postService = postService;
        this.memberService = memberService;
    }

    @GetMapping("/signUp")
    public String showSignUpForm(Model model) {
        model.addAttribute("memberDto", new Member("username", "email", "password")); // A
        return "signUp"; // This should be the name of the Thymeleaf template that contains the form
    }

    @GetMapping("/addPost")
    public String showAddPostForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            //뒤에-user이 붙음..
            currentUsername = authentication.getName(); // Gets the username of the currently logged-in user
        }
        currentUsername = memberService.findByUsername(currentUsername).getEmail();
        PostDto postDto = new PostDto("제목", "내용", currentUsername, "test"); //videoId 아무거나 씀 나중에 체크
        postDto.setUsername(currentUsername); // Set the username in PostDto

        model.addAttribute("postDto", postDto);
        return "addPost";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @GetMapping("/chat")
    public String showChat(@RequestParam String roomId, @RequestParam String userUUID, Model model,Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("roomId", roomId);
        model.addAttribute("userUUID",userUUID);
        model.addAttribute("username", username);
        return "chat";
    }

    //메인화면. 캘린더 API와 합치기 시도중
    @GetMapping("/{username}/home")
    public String home(@PathVariable String username, Model model, HttpServletRequest request) {
        // 날짜별로 글 리스트 저장하는 HashMap
        Map<LocalDate, List<Post>> postsMap = new HashMap<>();
        Member profileUser = memberService.findByEmail(username).get();
        Member loggedInUser = memberService.getLoggedInUser(request); // Implement this method to identify the logged-in user
        boolean isFriend = memberService.isFriend(profileUser.getId(), loggedInUser.getId()); // Implement this method to check friendship

        //이부분 조금만 수정! 중요
        if (!profileUser.getUsername().equals(loggedInUser.getUsername()) && !isFriend) {
            // Redirect if not the owner or a friend
            return "redirect:/recommendedFriends";
        }

        // Initialize days array
        LocalDate lastStart= LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastEnd = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
        List<Post> lastPosts = postService.getPostsByDateRangeAndMember(lastStart, lastEnd, username);
        int[] lastDays = new int[lastEnd.getDayOfMonth()];

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<Post> posts = postService.getPostsByDateRangeAndMember(start, end, username);
        int[] days = new int[end.getDayOfMonth()];


        // Update days array based on posts
        for (Post post : lastPosts) {
            LocalDate date = post.getDate();
            if (date.getMonth().equals(LocalDate.now().minusMonths(1).getMonth())) {
                lastDays[date.getDayOfMonth() - 1] = 1;
            }
        }
        for (Post post : posts) {
            LocalDate date = post.getDate();
            if (date.getMonth().equals(LocalDate.now().getMonth())) {
                days[date.getDayOfMonth() - 1] = 1;
            }
        }
        // Add to model
        model.addAttribute("lastDays", lastDays);
        model.addAttribute("days", days);

        return "home";
    }

    @GetMapping({"/", "/index"})
    public String calendar(Model model) {
            return "redirect:/login";
        //    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //    if (auth instanceof AnonymousAuthenticationToken) {
        //    // User not logged in
        //return "redirect:/login";
        //   } else {
        // change to /{username}/home
        //    return "redirect:/home";
        // }
    }

    //특정 날짜 글 조회
    @GetMapping("/home/{username}/{date}")
    public String dailyPosts(@PathVariable String username, @PathVariable String date, Model model,
                             HttpServletRequest request) {
        try {
            //친구일 때만 방문 가능!
            Member profileUser = memberService.findByEmail(username).get();
            Member loggedInUser = memberService.getLoggedInUser(request); // Implement this method to identify the logged-in user
            boolean isFriend = memberService.isFriend(profileUser.getId(), loggedInUser.getId()); // Implement this method to check friendship

            //친구 아니라면 친구추가 페이지로
            if (!profileUser.getUsername().equals(loggedInUser.getUsername()) && !isFriend) {
                return "redirect:/recommendedFriends";
            }
            // Define a DateTimeFormatter to specify the input date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            List<Post> posts = postService.getPostsByDateRangeAndMember(localDate, localDate, username);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUsername = authentication.getName(); // Retrieves the username of the logged-in user
            String loggedInUserEmail = memberService.findByUsername(loggedInUsername).getEmail();

            model.addAttribute("loggedInUsername", loggedInUserEmail);
            model.addAttribute("posts", posts);
            model.addAttribute("date", date);

        } catch (Exception e) {
            model.addAttribute("error", "Invalid date or no posts available.");
        }
        return "daily_posts";
    }

    @GetMapping("/editPost/{postId}")
    public String showEditPostForm(@PathVariable long postId, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUsername = authentication.getName(); // Gets the username of the currently logged-in user
        }
        Post post = postService.getPostById(postId);
        currentUsername = memberService.findByUsername(currentUsername).getEmail();
        if (!post.getCreator().equals(currentUsername))
            return "redirect:/" + currentUsername + "/" + "home/";
        PostDto postDto = new PostDto("제목", "내용", currentUsername, postId);
        postDto.setUsername(currentUsername); // Set the username in PostDto

        model.addAttribute("postDto", postDto);
        return "editPost";
    }

    @GetMapping("/recommendedFriends")
    public String showRecommendedFriends(Model model) {
        // Get the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Assuming a method getNonFriendsMembers that returns a list of members who are not friends with the given username
        List<Member> nonFriendMembers = memberService.getNonFriendsMembers(currentUsername);

        // Add the list to the model
        model.addAttribute("members", nonFriendMembers);
        model.addAttribute("currentUsername", currentUsername);
        return "recommendedFriends";
    }

    @GetMapping("/showAndManageFriends")
    public String showAndManageFriends(Model model) {
        // Get the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Assuming a method getNonFriendsMembers that returns a list of members who are not friends with the given username
        List<Member> nonFriendMembers = memberService.getFriendList(currentUsername);

        // Add the list to the model
        model.addAttribute("members", nonFriendMembers);
        model.addAttribute("currentUsername", currentUsername);
        return "showAndManageFriends";
    }

    @GetMapping("/receivedFriendRequests")
    public String showReceivedFriendRequests(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Member currentUser = memberService.findByUsername(currentUsername);
        Set<Member> friendRequests = memberService.findReceivedFriendRequests(currentUsername);
        //if(!friendRequests.isEmpty())
        model.addAttribute("friendRequests", friendRequests);
        model.addAttribute("currentUsername", currentUsername);
        return "friend_requests";
    }

}
