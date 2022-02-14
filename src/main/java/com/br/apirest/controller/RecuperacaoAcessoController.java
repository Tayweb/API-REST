package com.br.apirest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apirest.ObjetoError;
import com.br.apirest.model.Usuario;
import com.br.apirest.repository.UsuarioRepository;
import com.br.apirest.service.EnviaEmailService;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperacaoAcessoController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EnviaEmailService enviaEmailService;

	@PostMapping(value = "/")
	public ResponseEntity<ObjetoError> recuperarAcesso(@RequestBody Usuario login) throws Exception {
		ObjetoError objetoError = new ObjetoError();

		Usuario usuario = usuarioRepository.findByLogin(login.getLogin());

		if (usuario == null) {

			objetoError.setCodeError("404");
			objetoError.setError("Usuário não encontrado");

		} else {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime()); // Gera nova senha
			
			String senhaCriptografada= new BCryptPasswordEncoder().encode(senhaNova); //Criptografa a senha

			usuarioRepository.updateSenha(senhaCriptografada, usuario.getId()); // Atualiza no banco de dados

			enviaEmailService.enviarEmail("Recuperação de senha", usuario.getLogin(), "Sua nova senha é: " + senhaNova); //Envia nova senha pro email do usuario

			// Rotina de envio de email
			objetoError.setCodeError("200");
			objetoError.setError("Acesso enviado para o emial!");
		}

		return new ResponseEntity<ObjetoError>(objetoError, HttpStatus.OK);

	}
}
