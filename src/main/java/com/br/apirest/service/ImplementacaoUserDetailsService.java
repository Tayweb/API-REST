package com.br.apirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.br.apirest.model.Usuario;
import com.br.apirest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.findByLogin(username);

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}

		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}

	public void insereAcessoPadrao(Long id) {

		// Descobre a constraint de restrição
		String constraint = usuarioRepository.consultaConstraintRole();

		if (constraint != null) {
			// Remove a restrição
			jdbcTemplate.execute(" alter table usuarios_role drop constraint " + constraint);

		}

		// Insere o acesso padrão
		usuarioRepository.inserirAcessoRolePadrao(id);

	}

}
