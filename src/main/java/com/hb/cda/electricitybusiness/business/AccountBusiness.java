package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.controller.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccountBusiness {

    /**
     * Méthode d'inscription d'un nouveau userDTO qui va assigner un rôle par défaut, hasher
     * le mot de passe, faire persister le userDTO et envoyer un mail de validation contenant un code de validation
     * @param user Le nouveau userDTO à faire persister
     * @return Le userDTO persisté
     */

    User registerUser(User user);

    /**
     * Méthode qui va envoyer un lien avec un token permettant de créer un nouveau mot
     * de passe
     * @param email Le mail de la personne qui souhaite réinitialiser son mot de passe
     */
    void resetPassword(String email);


    User getAuthenticatedUserResponse(String email);

    User getUserById(Long id);

    List<User> getAllUsers();

    /**
     * Méthode permettant à un user de supprimer son compte, en accordance avec le RGPD
     *
     * @param id L'id du user souhaitant supprimer son compte
     */
    void deleteUser(Long id);

    /**
     * Méthode permettant à un user de mettre à jour son email
     *
     * @param id             L'id du user qui souhaite modifier son email
     * @param emailUpdateDto le nouvelle email du user
     */
    User updateUserEmail(Long id, UserEmailUpdateDto emailUpdateDto);

    /**
     * Méthode permettant à un user de modifier sa photo de profile
     * @param id L'id du user qui souhaite modifier sa photo de profile
     * @param file L'url source de la photo
     * @param altText Le texte alternatif de la photo
     * @param isMain La photo est la principale par défaut(true)
     */
    String uploadProfilePicture(Long id, MultipartFile file, String altText, boolean isMain);

}
