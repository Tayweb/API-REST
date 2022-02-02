package com.br.apirest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apirest.model.Usuario;
import com.br.apirest.model.UsuarioDTO;
import com.br.apirest.repository.UsuarioRepository;
import com.google.gson.Gson;

@CrossOrigin // Ao inserir essa anotação (@CrossOrigin), a api se tornará acessivel em
				// qualquer local
@RestController
@RequestMapping(value = "/usuarios")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping(value = "/buscar/{id}", produces = "application/json")
	//@Cacheable("cacheuser")	Esse não atualiza o cache
	@CacheEvict(value = "cacheusuario", allEntries = true) // Remove os caches que não são usados a bastante tempo
	@CachePut("cacheusuario") // Atualiza os cache
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity(new UsuarioDTO(usuario.get()), HttpStatus.OK);

	}

	/*
	 * Vamos supor que o carregamento de usuário seja um processo lento e queremos
	 * controlar ele com cache para agilizar o processo
	 */
	@GetMapping(value = "/listar", produces = "application/json", headers = "X-API-Version=v1") // Versionamento de // codigo
	@CacheEvict(value = "cacheusuario", allEntries = true)
	@CachePut("cacheusuario") // Atualiza os cache
	public ResponseEntity<List<Usuario>> listaUserV1() throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/listar", produces = "application/json", headers = "X-API-Version=v2") // Versionamento de
																								// codigo
	public ResponseEntity<List<Usuario>> listaUserV2() {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/salvar", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		
		//Consumindo API publica externa
		
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream iStream = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(iStream,"UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while ((cep = br.readLine()) !=null) {
			jsonCep.append(cep);
			
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());

		usuario.setSenha(senhaCriptografada);
		Usuario usuarios = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarios, HttpStatus.OK);

	}

	@PutMapping(value = "/atualizar", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findByLogin(usuario.getLogin());

		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarios = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarios, HttpStatus.OK);

	}

	@DeleteMapping(value = "/excluir/{id}", produces = "application/text")
	public String delete(@PathVariable(value = "id") Long id) {

		usuarioRepository.deleteById(id);

		return "Deletado com sucesso!";

	}

}
