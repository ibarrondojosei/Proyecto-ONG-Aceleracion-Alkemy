package com.alkemy.ong.service.impl;

import com.alkemy.ong.auth.service.UserDetailsCustomService;
import com.alkemy.ong.auth.utility.JwtUtils;
import com.alkemy.ong.exception.ParamNotFoundException;
import com.alkemy.ong.models.entity.CommentEntity;
import com.alkemy.ong.models.entity.RoleEntity;
import com.alkemy.ong.models.entity.UserEntity;
import com.alkemy.ong.models.mapper.CommentMapper;
import com.alkemy.ong.models.request.CommentRequest;
import com.alkemy.ong.models.response.CommentResponse;
import com.alkemy.ong.models.response.CommentShortResponse;
import com.alkemy.ong.models.response.UserDetailsResponse;
import com.alkemy.ong.repository.CommentRepository;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.CommentService;
import com.amazonaws.services.pinpoint.model.ForbiddenException;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;


    public CommentResponse create(CommentRequest request) {
        CommentEntity commentEntity = commentMapper.toEntity(request);
        commentEntity = commentRepository.save(commentEntity);
        return commentMapper.toResponse(commentEntity);
    }

    public List<CommentShortResponse> readComments() {
        List<CommentEntity> entities = commentRepository.findAllByTimestampAsc();
        List<CommentShortResponse>  responses = commentMapper.toShortResponseList(entities);
        return responses;
    }

    public void delete(Long id, String token) {
        if (! commentRepository.existsById(id)){
            throw new ParamNotFoundException("Comment (id = "+id+") not found");
        }
        token = token.substring(7);
        String username = jwtUtils.extractUsername(token);
        UserEntity userEntity = userRepository.findByEmail(username).get();
        Set<RoleEntity> roleEntities = userEntity.getRoleId();
        List<String> roles = roleEntities.stream().map(RoleEntity::getName).collect(Collectors.toList());
        if (roleEntities.contains("ADMIN")) {
            commentRepository.deleteById(id);
        }
        else {
            CommentEntity commentEntity = commentRepository.getById(id);
            if (commentEntity.getUser() != userEntity) {
                throw new ForbiddenException("Not authorized");
            }
            commentRepository.deleteById(id);
        }
    }

}
