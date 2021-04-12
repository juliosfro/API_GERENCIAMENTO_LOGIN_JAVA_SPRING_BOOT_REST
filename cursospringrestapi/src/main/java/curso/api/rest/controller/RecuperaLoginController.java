package curso.api.rest.controller;

import curso.api.rest.exception.ErrorDetails;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ServiceEnviaEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaLoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServiceEnviaEmail serviceEnviaEmail;

    @GetMapping(value = "/{login}")
    public ResponseEntity<ErrorDetails> recuperar(@PathVariable String login) throws Exception {

        Usuario user = new Usuario();
        user.setLogin(login);
        ErrorDetails errorDetails = new ErrorDetails();

        Usuario usuario = usuarioRepository.findUserByLogin(user.getLogin());

        if (usuario == null) {
            errorDetails.setCode("404"); /* Nao encontrado */
            errorDetails.setMessage("Usuário não encontrado.");
        } else {
            /* Rotina de envio de e-mail */
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String newPassword = simpleDateFormat.format(Calendar.getInstance().getTime());

            String newEncryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
            usuarioRepository.updatePassword(newEncryptedPassword, usuario.getId());

            serviceEnviaEmail.enviarEmail("Recuperação de senha", usuario.getLogin(), "Sua nova senha" +
                            " é: " + newPassword);

            errorDetails.setCode("200"); /* Encontrado */
            errorDetails.setMessage("Informações de acesso enviadas para o seu e-mail.");
        }
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.OK);
    }

}
