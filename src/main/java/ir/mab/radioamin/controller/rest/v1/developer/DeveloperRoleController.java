package ir.mab.radioamin.controller.rest.v1.developer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.DEVELOPER)
public class DeveloperRoleController {
    RoleRepository roleRepository;

    @Autowired
    public DeveloperRoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("/role")
    SuccessResponse<Iterable<Role>> getRoles(){
        return new SuccessResponse<>("roles", roleRepository.findAll());
    }
}
