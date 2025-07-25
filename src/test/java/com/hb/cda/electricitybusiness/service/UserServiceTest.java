package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.dto.UserResponse;
import com.hb.cda.electricitybusiness.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        //Initialiser les mocks avant chaque test
        MockitoAnnotations.openMocks(this);
    }


    // --- Tests pour la méthode loadUserByUsername ---
    /**
     * Test pour voir si l'utilisateur est trouvé par son email
     * Description : On veut vérifier que si on demande un utilisateur par son email
     * et qu'il existe, la méthode le trouve bien et nous donne ses infos.
     */
    @Test
    void testLoadUserByUsername_found() {
        //Définir les données de test
        String email = "test@postman.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("testPostman");
        //Lorsque la méthode `findByEmail` est appelée, Alors, cette méthode doit retourner un `Optional` contenant `user`
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        //Exécution de la vraie méthode à tester
        UserDetails userDetails = userService.loadUserByUsername(email);
        //Vérification des résultats (Assertions)
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("testPostman", userDetails.getPassword());
        //Vérification des interactions avec les Mocks
        verify(userRepository,times(1)).findByEmail(email);
    }


    /**
     * Test pour voir si l'utilisateur n'est pas trouvé par son email
     * Description : On vérifie que si l'email n'existe pas, la méthode réagit comme il faut
     * en nous disant que l'utilisateur n'a pas été trouvé.
     */
    @Test
    void testLoadByUsername_notFound() {
        String email = "notExiste@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
        verify(userRepository, times(1)).findByEmail(email);
    }

    // --- Tests pour la méthode registerUser ---

    /**
     * Test pour voir si : Un nouvel utilisateur s'enregistre avec succès
     * Description : On vérifie que la méthode peut créer un nouvel utilisateur
     * quand tout est bon (email pas déjà pris, etc.).
     */
    @Test
    void testRegister_success() {
        //Préparer les infos du nouvel utilisateur
        RegisterRequest registerRequest = new RegisterRequest("dupuis@gmail.com","password123", "Dupuis", "Marc", "92 rue du test", "69800", "St priest", null,null,null);

        //Indiquer que ce mail n'est pas existant
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        //Indiquer comment doit être chiffré le password
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        //Créer un faux utilisateur avec les infos du nouveau user
        User saveUser = new User();
        saveUser.setId(1L);
        saveUser.setEmail(registerRequest.getEmail());
        saveUser.setPassword("encodedPassword");
        saveUser.setName(registerRequest.getName());
        saveUser.setFirstName(registerRequest.getFirstName());
        saveUser.setAddress(registerRequest.getAddress());
        saveUser.setPostaleCode(registerRequest.getPostaleCode());
        saveUser.setCity(registerRequest.getCity());
        saveUser.setPhoneNumber(registerRequest.getPhoneNumber());
        saveUser.setOwnsStation(false);
        saveUser.setCodeCheck(null);

        //Quand on save n'importe quel user, on retourne le faux user
        when(userRepository.save(any(User.class))).thenReturn(saveUser);

        //Utilisation de la vrai methode
        User result = userService.registerUser(registerRequest);

        //Vérification que le user à bien été créé
        assertNotNull(result);
        assertEquals(1L,result.getId());
        assertEquals(registerRequest.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        //Vérification des interactions avec les mocks
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Test pour voir si : L'utilisateur ne peut pas s'enregistrer si l'email existe déjà
     * Description : On vérifie que la méthode empêche la création d'un compte
     * si l'email est déjà utilisé et qu'elle nous avertit.
     */
    @Test
    void testRegisterUser_emailAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest(
                "existing@email.com", "John", "Doe", "password123",
                "123 Main St", "12345", "CityVille", null, null, "555-1234");

        // On dit au faux userRepository que cet email est DÉJÀ pris.
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        // On s'attend à ce qu'une erreur spécifique (IllegalArgumentException) soit levée.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(registerRequest); // On essaie d'enregistrer l'utilisateur.
        });

        assertTrue(thrown.getMessage().contains("existe déjà"));
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail()); // On a bien vérifié l'email.
        verify(passwordEncoder, never()).encode(anyString()); // Le mot de passe NE DOIT PAS être chiffré.
        verify(userRepository, never()).save(any(User.class)); // L'utilisateur NE DOIT PAS être sauvegardé.
    }

    // --- Tests pour la méthode `deleteUser` (pour supprimer un utilisateur) ---

    /**
     * Test pour voir si : Suppression d'un utilisateur avec une image de profil par défaut
     * Description : On vérifie que l'utilisateur est bien supprimé, et que si sa
     * photo est une image par défaut, on ne tente pas de la supprimer de notre stockage.
     */
    @Test
    void testDeleteUser_success_withDefaultPicture() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        // On donne à l'utilisateur une photo de profil par défaut.
        PictureDetailsDTO defaultPicture = new PictureDetailsDTO("alt", "images/default_avatar.png", true);
        user.setProfilePicture(defaultPicture);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true); // Le repository dit que l'utilisateur existe.

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(uploadService, never()).removeExisting(anyString()); // Très important : ne pas supprimer la photo par défaut.
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId); // Vérifie la suppression.
    }

    /**
     * Test pour voir si : Suppression d'un utilisateur avec une image de profil personnalisée
     * Description : On vérifie que l'utilisateur est bien supprimé, et que si sa
     * photo est une image personnalisée, on la supprime aussi de notre stockage.
     */
    @Test
    void testDeleteUser_success_withCustomPicture() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        // On donne à l'utilisateur une photo de profil personnalisée.
        PictureDetailsDTO customPicture = new PictureDetailsDTO("alt", "uploads/custom_image.jpg", false);
        user.setProfilePicture(customPicture);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(uploadService, times(1)).removeExisting("uploads/custom_image.jpg"); // L'image personnalisée DOIT être supprimée.
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    /**
     * Test pour voir si : Suppression d'un utilisateur sans image de profil
     * Description : On vérifie que l'utilisateur est bien supprimé, et qu'il n'y a
     * pas d'erreur si l'utilisateur n'avait pas de photo de profil.
     */
    @Test
    void testDeleteUser_noPicture() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setProfilePicture(null); // Pas de photo de profil.

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(uploadService, never()).removeExisting(anyString()); // On ne doit pas essayer de supprimer une image.
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    /**
     * Test pour voir si : Suppression d'un utilisateur introuvable
     * Description : On vérifie que si on essaie de supprimer un utilisateur qui n'existe pas,
     * la méthode le signale par une erreur.
     */
    @Test
    void testDeleteUser_notFound() {
        Long userId = 99L; // Un ID qui n'existe pas.
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(uploadService, never()).removeExisting(anyString()); // Aucune suppression d'image.
        verify(userRepository, never()).existsById(anyLong()); // Pas besoin de vérifier l'existence si pas trouvé.
        verify(userRepository, never()).deleteById(anyLong()); // Pas de suppression en BDD.
    }

    // --- Tests pour la méthode `updateUser` (pour modifier les infos d'un utilisateur) ---

    /**
     * Test pour voir si : Mise à jour de l'utilisateur - Succès (email inchangé, mot de passe mis à jour)
     * Description : On vérifie que l'utilisateur est mis à jour correctement, y compris
     * le nouveau mot de passe (qui doit être chiffré), si son email ne change pas.
     */
    @Test
    void testUpdateUser_success_emailNotChanged_passwordUpdated() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("original@email.com");
        existingUser.setPassword("oldPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("original@email.com");
        updateRequest.setPassword("newPassword");
        updateRequest.setFirstName("UpdatedFirstName");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.updateUserFromRequest(any(UserUpdateRequest.class), any(User.class))).thenAnswer(invocation -> {
            User userToUpdate = invocation.getArgument(1);
            userToUpdate.setName("UpdatedFirstName");
            userToUpdate.setFirstName("UpdatedLastName");
            return null;
        });

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("original@email.com");
        userResponse.setName("UpdatedFirstName");
        userResponse.setFirstName("UpdatedLastName");
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals("original@email.com", result.getEmail());
        assertEquals("newEncodedPassword", existingUser.getPassword());
        assertEquals("UpdatedFirstName", existingUser.getName());
        assertEquals("UpdatedLastName", existingUser.getFirstName());

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUserFromRequest(updateRequest, existingUser);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(existingUser);
        verify(userRepository, never()).findByEmail(anyString());
    }

    /**
     * Test pour voir si : Mise à jour de l'utilisateur - Succès (email changé, pas de nouveau mot de passe)
     * Description : On vérifie que l'email est mis à jour si le nouvel email est disponible,
     * et que l'ancien mot de passe est conservé si aucun nouveau n'est fourni.
     */
    @Test
    void testUpdateUser_success_emailChanged_noPasswordUpdate() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("original@email.com");
        existingUser.setPassword("oldEncodedPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("new_email@email.com");
        updateRequest.setPassword("");
        updateRequest.setFirstName("UpdatedFirstName");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new_email@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.updateUserFromRequest(any(UserUpdateRequest.class), any(User.class))).thenAnswer(invocation -> {
            User userToUpdate = invocation.getArgument(1);
            userToUpdate.setFirstName("UpdatedFirstName");
            return null;
        });
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("new_email@email.com");
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals("new_email@email.com", result.getEmail());
        assertEquals("oldEncodedPassword", existingUser.getPassword());
        assertEquals("UpdatedFirstName", existingUser.getFirstName());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("new_email@email.com");
        verify(userMapper, times(1)).updateUserFromRequest(updateRequest, existingUser);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(existingUser);
    }

    /**
     * Test pour voir si : Mise à jour de l'utilisateur - Email changé mais déjà pris
     * Description : On vérifie que la méthode lève une erreur si on essaie
     * de changer l'email de l'utilisateur pour un email déjà utilisé par quelqu'un d'autre.
     */
    @Test
    void testUpdateUser_emailChanged_alreadyUsed() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("original@email.com");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("already_used@email.com");

        User anotherUserWithSameEmail = new User();
        anotherUserWithSameEmail.setId(2L);
        anotherUserWithSameEmail.setEmail("already_used@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // On dit que le nouvel email est DÉJÀ utilisé.
        when(userRepository.findByEmail("already_used@email.com")).thenReturn(Optional.of(anotherUserWithSameEmail));

        // On s'attend à une erreur (IllegalArgumentException).
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        assertTrue(thrown.getMessage().contains("L'email already_used@email.com est déjà utilisé."));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("already_used@email.com");
        verify(userMapper, never()).updateUserFromRequest(any(UserUpdateRequest.class), any(User.class));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test pour voir si : Mise à jour de l'utilisateur - Utilisateur introuvable
     * Description : On vérifie que la méthode signale une erreur si on essaie de
     * mettre à jour un utilisateur qui n'existe pas avec l'ID donné.
     */
    @Test
    void testUpdateUser_userNotFound() {
        Long userId = 99L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userMapper, never()).updateUserFromRequest(any(UserUpdateRequest.class), any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


    // --- Tests pour la méthode `updateUserEmail` (pour changer juste l'email de l'utilisateur) ---

    /**
     * Test pour voir si : Mise à jour de l'email utilisateur - Succès (email changé)
     * Description : On vérifie que l'email de l'utilisateur est mis à jour
     * si le nouvel email est valide et n'est pas déjà pris.
     */
    @Test
    void testUpdateUserEmail_success_emailChanged() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@email.com");

        UserEmailUpdateDto emailUpdateDto = new UserEmailUpdateDto("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(emailUpdateDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("new@email.com");
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.updateUserEmail(userId, emailUpdateDto);

        assertNotNull(result);
        assertEquals("new@email.com", result.getEmail());
        assertEquals("new@email.com", existingUser.getEmail()); // L'email de l'utilisateur doit être mis à jour.

        verify(userRepository, times(1)).findById(userId); // On a cherché l'utilisateur.
        verify(userRepository, times(1)).findByEmail(emailUpdateDto.getEmail()); // On a vérifié la disponibilité de l'email.
        verify(userRepository, times(1)).save(existingUser); // L'utilisateur mis à jour a été sauvegardé.
        verify(userMapper, times(1)).userToUserResponse(existingUser); // Le mapper a été appelé.
    }

    /**
     * Test pour voir si : Mise à jour de l'email utilisateur - Pas de changement
     * Description : On vérifie que la méthode ne fait rien (pas de sauvegarde)
     * si le nouvel email est identique à l'ancien.
     */
    @Test
    void testUpdateUserEmail_noChange() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@email.com");

        UserEmailUpdateDto emailUpdateDto = new UserEmailUpdateDto("old@email.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("old@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userToUserResponse(existingUser)).thenReturn(userResponse);

        UserResponse result = userService.updateUserEmail(userId, emailUpdateDto);

        assertNotNull(result);
        assertEquals("old@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, times(1)).userToUserResponse(existingUser);
    }

    /**
     * Test pour voir si : Mise à jour de l'email utilisateur - Email déjà pris
     * Description : On vérifie que la méthode signale une erreur si le nouvel email
     * est déjà utilisé par un autre compte.
     */
    @Test
    void testUpdateUserEmail_alreadyUsed() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("original@email.com");

        UserEmailUpdateDto emailUpdateDto = new UserEmailUpdateDto("already_used@email.com");

        User anotherUserWithSameEmail = new User();
        anotherUserWithSameEmail.setId(2L);
        anotherUserWithSameEmail.setEmail("already_used@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(emailUpdateDto.getEmail())).thenReturn(Optional.of(anotherUserWithSameEmail));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserEmail(userId, emailUpdateDto);
        });

        assertTrue(thrown.getMessage().contains("L'email already_used@email.com est déjà utilisé."));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(emailUpdateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponse(any(User.class));
    }

    /**
     * Test pour voir si : Mise à jour de l'email utilisateur - Utilisateur introuvable
     * Description : On vérifie que la méthode signale une erreur si l'utilisateur
     * à modifier n'existe pas.
     */
    @Test
    void testUpdateUserEmail_userNotFound() {
        Long userId = 99L;
        UserEmailUpdateDto emailUpdateDto = new UserEmailUpdateDto("test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.updateUserEmail(userId, emailUpdateDto);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponse(any(User.class));
    }

}
