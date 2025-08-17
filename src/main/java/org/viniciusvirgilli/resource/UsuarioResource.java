package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.viniciusvirgilli.dto.UsuarioAtualizacaoDto;
import org.viniciusvirgilli.dto.UsuarioCriacaoDto;
import org.viniciusvirgilli.dto.UsuarioResponseDto;
import org.viniciusvirgilli.dto.LoginDto;
import org.viniciusvirgilli.model.Usuario;
import org.viniciusvirgilli.service.UsuarioService;
import org.viniciusvirgilli.service.ValidaUsuarioService;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuários", description = "Operações relacionadas ao gerenciamento de usuários")
public class UsuarioResource {

    private static final Logger LOG = Logger.getLogger(UsuarioResource.class);

    @Inject
    UsuarioService usuarioService;

    @Inject
    ValidaUsuarioService validaUsuarioService;

    @POST
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Usuário criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @APIResponse(responseCode = "409", description = "Usuário com este email já existe"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response criarUsuario(UsuarioCriacaoDto dto) {
        try {
            LOG.infof("Requisição para criar usuário: %s", dto.getEmail());

            validaUsuarioService.validarCriacao(dto);
            Usuario usuario = usuarioService.criarUsuario(dto);

            LOG.infof("Usuário criado com sucesso: ID=%d, Email=%s", usuario.id, usuario.getEmail());

            return Response.status(Response.Status.CREATED).entity(UsuarioResponseDto.fromEntity(usuario)).build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro de validação ao criar usuário: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro interno ao criar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao criar usuário").build();
        }
    }

    @GET
    @Operation(summary = "Listar usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class)))
    public List<UsuarioResponseDto> listarUsuarios(
            @Parameter(description = "Filtrar apenas usuários ativos") @QueryParam("ativos") @DefaultValue("false") boolean apenasAtivos) {
        try {
            LOG.info("Requisição para listar usuários recebida");

            List<Usuario> usuarios;
            if (apenasAtivos) {
                usuarios = usuarioService.listarAtivos();
            } else {
                usuarios = usuarioService.listarTodos();
            }
            return usuarios.stream()
                .map(UsuarioResponseDto::fromEntity)
                .toList();
        } catch (Exception e) {
            LOG.error("Erro ao listar usuários", e);
            throw new InternalServerErrorException("Erro interno ao listar usuários");
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Response buscarUsuarioPorId(
            @Parameter(description = "ID do usuário", required = true) @PathParam("id") Long id) {
        try {
            LOG.infof("Requisição para buscar usuário por ID: %d", id);

            Usuario usuario = usuarioService.buscarPorId(id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado").build();
            }

            return Response.ok(UsuarioResponseDto.fromEntity(usuario)).build();
        } catch (Exception e) {
            LOG.error("Erro ao buscar usuário por ID", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar usuário").build();
        }
    }

    @GET
    @Path("/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário específico pelo seu email")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Response buscarUsuarioPorEmail(
            @Parameter(description = "Email do usuário", required = true) @PathParam("email") String email) {
        try {
            LOG.infof("Requisição para buscar usuário por email: %s", email);

            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado").build();
            }

            return Response.ok(UsuarioResponseDto.fromEntity(usuario)).build();
        } catch (Exception e) {
            LOG.error("Erro ao buscar usuário por email", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar usuário").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário específico")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response atualizarUsuario(
            @Parameter(description = "ID do usuário", required = true) @PathParam("id") Long id,
            UsuarioAtualizacaoDto dto) {
        try {
            LOG.infof("Requisição para atualizar usuário: ID=%d", id);

            validaUsuarioService.validarAtualizacao(dto);
            Usuario usuario = usuarioService.atualizarUsuario(id, dto);

            LOG.infof("Usuário atualizado com sucesso: ID=%d", id);

            return Response.ok(UsuarioResponseDto.fromEntity(usuario)).build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro de validação ao atualizar usuário: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro interno ao atualizar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar usuário").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response deletarUsuario(
            @Parameter(description = "ID do usuário", required = true) @PathParam("id") Long id) {
        try {
            LOG.infof("Requisição para deletar usuário: ID=%d", id);

            usuarioService.deletarUsuario(id);

            LOG.infof("Usuário deletado com sucesso: ID=%d", id);

            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro ao deletar usuário: %s", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro interno ao deletar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao deletar usuário").build();
        }
    }

    @PUT
    @Path("/{id}/desativar")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário sem removê-lo do sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário desativado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response desativarUsuario(
            @Parameter(description = "ID do usuário", required = true) @PathParam("id") Long id) {
        try {
            LOG.infof("Requisição para desativar usuário: ID=%d", id);

            Usuario usuario = usuarioService.desativarUsuario(id);

            LOG.infof("Usuário desativado com sucesso: ID=%d", id);

            return Response.ok(UsuarioResponseDto.fromEntity(usuario)).build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro ao desativar usuário: %s", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro interno ao desativar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao desativar usuário").build();
        }
    }

    @PUT
    @Path("/{id}/ativar")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário previamente desativado")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário ativado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response ativarUsuario(
            @Parameter(description = "ID do usuário", required = true) @PathParam("id") Long id) {
        try {
            LOG.infof("Requisição para ativar usuário: ID=%d", id);

            Usuario usuario = usuarioService.ativarUsuario(id);

            LOG.infof("Usuário ativado com sucesso: ID=%d", id);

            return Response.ok(usuario).build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro ao ativar usuário: %s", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro interno ao ativar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao ativar usuário").build();
        }
    }

    @POST
    @Path("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário com email e senha")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
        @APIResponse(responseCode = "401", description = "Credenciais inválidas"),
        @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response login(LoginDto loginDto) {
        try {
            LOG.infof("Tentativa de login para: %s", loginDto.getEmail());

            if (loginDto.getEmail() == null || loginDto.getSenha() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Email e senha são obrigatórios").build();
            }

            Usuario usuario = usuarioService.autenticar(loginDto.getEmail(), loginDto.getSenha());
            
            if (usuario == null) {
                LOG.warnf("Falha na autenticação para: %s", loginDto.getEmail());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Credenciais inválidas").build();
            }

            LOG.infof("Login realizado com sucesso para: %s", loginDto.getEmail());
            return Response.ok(UsuarioResponseDto.fromEntity(usuario)).build();
            
        } catch (Exception e) {
            LOG.error("Erro interno durante autenticação", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno durante autenticação").build();
        }
    }
}