package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.filter.ProfileFilterDTO;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.managers.ProfileManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    ProfileManager profileManager;

    @PostMapping
    public @ResponseBody ResponseEntity<OperationResponse> save(@RequestBody ProfileDTO dto, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = profileManager.manageOperation(dto, Operation.CREATE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public @ResponseBody ResponseEntity<OperationResponse> update(@RequestBody ProfileDTO dto, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = profileManager.manageOperation(dto, Operation.MODIFY, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    public @ResponseBody ResponseEntity<OperationResponse> delete(@PathVariable String name, HttpServletRequest request) {
        String username = getUsername(request);
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(name);
        OperationResponse response = profileManager.manageOperation(profileDTO, Operation.REMOVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = profileManager.findAll(new Profile(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/usable")
    public @ResponseBody ResponseEntity<OperationResponse> findAllUsableProfiles(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = profileManager.findAllUsable(new Profile(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalProfiles(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = profileManager.findAllNeedsApproval(new Profile(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval/{name}")
    public @ResponseBody ResponseEntity<OperationResponse> findApprovalByName(@PathVariable String name, HttpServletRequest request) {
        String username = getUsername(request);
        Profile profile = new Profile();
        profile.setName(name);
        OperationResponse response = profileManager.findNeedsApproval(profile, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping({"/{name}"})
    public @ResponseBody ResponseEntity<OperationResponse> findByName(@PathVariable String name, HttpServletRequest request) {
        String username = getUsername(request);
        Profile profile = new Profile();
        profile.setName(name);
        OperationResponse response = profileManager.findByDiscriminant(profile, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/approve/{name}")
    public @ResponseBody ResponseEntity<OperationResponse> approve(@PathVariable String name, HttpServletRequest request) {
        String username = getUsername(request);
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(name);
        OperationResponse response = profileManager.manageOperation(profileDTO, Operation.APPROVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reject/{name}")
    public @ResponseBody ResponseEntity<OperationResponse> reject(@PathVariable String name, HttpServletRequest request) {
        String username = getUsername(request);
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(name);
        OperationResponse response = profileManager.manageOperation(profileDTO, Operation.REJECT, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public @ResponseBody ResponseEntity<OperationResponse> findFilteredProfiles(@RequestBody ProfileFilterDTO profileFilterDTO) {
        OperationResponse response = profileManager.filter(profileFilterDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
