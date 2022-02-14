package com.br.apirest.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

import com.br.apirest.model.Profissao;
import com.br.apirest.model.UserChart;
import com.br.apirest.model.UserPeport;
import com.br.apirest.model.Usuario;
import com.br.apirest.repository.ProfissaoRepository;
import com.br.apirest.repository.TelefoneRepository;
import com.br.apirest.repository.UsuarioRepository;
import com.br.apirest.service.ImplementacaoUserDetailsService;
import com.br.apirest.service.RelatorioService;

@CrossOrigin // Ao inserir essa anotação (@CrossOrigin), a api se tornará acessivel em
				// qualquer local
@RestController
@RequestMapping(value = "/usuarios")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private RelatorioService relatorioService;

	@Autowired
	private JdbcTemplate jdbcTemplate; // Parte interna do JDBC, ele trabalha mais perto do SQL puro do que o spring
										// data

//PADRÃO DTO
//	@GetMapping(value = "/buscar/{id}", produces = "application/json")
////	//@Cacheable("cacheuser")	Esse não atualiza o cache
////	@CacheEvict(value = "cacheusuario", allEntries = true) // Remove os caches que não são usados a bastante tempo
////	@CachePut("cacheusuario") // Atualiza os cache
//	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {
//
//		Optional<Usuario> usuario = usuarioRepository.findById(id);
//
//		return new ResponseEntity(new UsuarioDTO(usuario.get()), HttpStatus.OK);
//
//	}

	@GetMapping(value = "/buscar/{id}", produces = "application/json")
//	//@Cacheable("cacheuser")	Esse não atualiza o cache
//	@CacheEvict(value = "cacheusuario", allEntries = true) // Remove os caches que não são usados a bastante tempo
//	@CachePut("cacheusuario") // Atualiza os cache
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@CacheEvict(value = "cacheusuario", allEntries = true) // Remove os caches que não são usados a bastante tempo
	@CachePut("cacheusuario") // Atualiza os cache
	@GetMapping(value = "/buscarpornome/{nome}", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findByNome(nome);

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@CacheEvict(value = "cacheusuario", allEntries = true) // Remove os caches que não são usados a bastante tempo
	@CachePut("cacheusuario") // Atualiza os cache
	@GetMapping(value = "/buscarpornomepage/{nome}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarioPorNomePage(@PathVariable("nome") String nome) {

		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {

			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));

			list = usuarioRepository.findAll(pageRequest);

		} else {

			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	/*
	 * Vamos supor que o carregamento de usuário seja um processo lento e queremos
	 * controlar ele com cache para agilizar o processo
	 */
//	@GetMapping(value = "/listar", produces = "application/json", headers = "X-API-Version=v1") // Versionamento de // codigo
//	@CacheEvict(value = "cacheusuario", allEntries = true)
//	@CachePut("cacheusuario") // Atualiza os cache
//	public ResponseEntity<List<Usuario>> listaUserV1() throws InterruptedException {
//		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
//		
//		
//
//		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
//	}

	@GetMapping(value = "/listar", produces = "application/json") // Versionamento de
																	// codigo
	public ResponseEntity<Page<Usuario>> listaUserV2() {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/listarporpage/{pagina}", produces = "application/json")

	public ResponseEntity<Page<Usuario>> listaUserV3(@PathVariable("pagina") int pagina) {

		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/salvar", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

//		// Consumindo API publica externa
//
//		URL url = new URL("http://viacep.com.br/ws/"+usuario.getCep()+"/json/");
//		URLConnection connection = url.openConnection();
//		InputStream iStream = connection.getInputStream();
//		BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
//
//		String cep = "";
//		StringBuilder jsonCep = new StringBuilder();
//
//		while ((cep = br.readLine()) != null) {
//			jsonCep.append(cep);
//
//		}
//
//		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
//
//		usuario.setCep(userAux.getCep());
//		usuario.setLogradouro(userAux.getLogradouro());
//		usuario.setComplemento(userAux.getComplemento());
//		usuario.setBairro(userAux.getBairro());
//		usuario.setLocalidade(userAux.getLocalidade());
//		usuario.setUf(userAux.getUf());

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());

		usuario.setSenha(senhaCriptografada);
		Usuario usuarios = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarios.getId());

		return new ResponseEntity<Usuario>(usuarios, HttpStatus.OK);

	}

	@PutMapping(value = "/atualizar", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarios = usuarioRepository.save(usuario);
		implementacaoUserDetailsService.insereAcessoPadrao(usuarios.getId());

		return new ResponseEntity<Usuario>(usuarios, HttpStatus.OK);

	}

	@DeleteMapping(value = "/excluir/{id}", produces = "application/text")
	public String delete(@PathVariable(value = "id") Long id) {

		usuarioRepository.deleteById(id);

		return "Deletado com sucesso!";

	}

	@DeleteMapping(value = "/excluirtelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable(value = "id") Long id) {

		telefoneRepository.deleteById(id);

		return "Deletado com sucesso!";

	}

	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		byte[] pdf = relatorioService.gerarRelatorio("relatorio-usuario", request.getServletContext()); // pega o
																										// caminho do
																										// relatorio

		String base64pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return new ResponseEntity<String>(base64pdf, HttpStatus.OK);
	}

	// Busca Por parametro
	@PostMapping(value = "/relatorioporparam", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request, @RequestBody UserPeport userReport)
			throws Exception {
		byte[] pdf = relatorioService.gerarRelatorio("relatorio-usuario", request.getServletContext()); // pega o
																										// caminho do
																										// relatorio

		String base64pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return new ResponseEntity<String>(base64pdf, HttpStatus.OK);
	}

	@GetMapping(value = "/grafico", produces = "application/json")
	public ResponseEntity<UserChart> grafico() {

		UserChart userChart = new UserChart();

		List<String> resultado = jdbcTemplate.queryForList(
				"select array_agg(''''||nome ||'''') from  usuario where salario > 0 and nome <> '' union all select cast (array_agg(salario) as character varying[]) from usuario where salario > 0 and nome <> ''",
				String.class);

		if (!resultado.isEmpty()) {
			String nome = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salario = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");

			userChart.setNome(nome);
			userChart.setSalario(salario);
		}

		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
	}

}
