package fpt.g36.gapms.service;


import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.impls.RoleServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

     @BeforeAll
     static void setUp() {
         System.out.println("RoleServiceImplTest");
     }


    @Test
    void getRoleByNameSuccess() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        Role role1 = new Role();
        role1.setId(2L);
        role1.setName("USER");


      Mockito.when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

       Optional<Role> result = roleService.getRole("ADMIN");

       assertTrue(result.isPresent());
       assertEquals("ADMIN", result.get().getName());

    }

    @Test
    void getRoleByNameFalse() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        Mockito.when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());
        Optional<Role> result = roleService.getRole("CUSTOMER");
        assertFalse(result.isPresent());

    }


     @Test
     void getAllRole(){
         Role role_1 = new Role();
         role_1.setId(1L);
         role_1.setName("ADMIN");

         Role role_2 = new Role();
         role_2.setId(2L);
         role_2.setName("CUSTOMER");

         List<Role> roles = Arrays.asList(role_1, role_2);

         Mockito.when(roleRepository.findAll()).thenReturn(roles);

         List<Role> result = roleService.getAllRoles();

         assertEquals(2, result.size());
         assertEquals("ADMIN", result.get(0).getName());
         assertEquals("CUSTOMER", result.get(1).getName());
     }
    @AfterAll
    static void tearDown() {
        System.out.println("RoleServiceImplTest");
    }
}
