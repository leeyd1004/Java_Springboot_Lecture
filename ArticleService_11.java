package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
//import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public ArrayList<Article> index(){
        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleForm dto) {
        log.info(dto.toString());
        Article articleEntity = dto.toEntity();
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        if(target != null){
            log.info("이미 존재하는 id 입니다.");
            return null;
        }
        return articleRepository.save(articleEntity);
    }

    public Article update(Long id, ArticleForm dto) {
        Article article = dto.toEntity();
        log.info("id:{}, article:{}",id,article.toString());
        Article target = articleRepository.findById(id).orElse(null);
        if(target == null || id != article.getId()){
            log.info("잘못된 요청 --> id:{}, article:{}",id,article.toString());
            return null;
        }
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated;
    }

    public Article delete(Long id) {
        Article target = articleRepository.findById(id).orElse(null);
        if(target == null){
            log.info("존재하지 않는 데이터 삭제 입니다.");
            return null;
        }
        articleRepository.delete(target);
        return target;
    }

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
//        List<Article> articleList = new ArrayList<>();
//        for(int i=0;i<dtos.size();i++){
//            ArticleForm dto = dtos.get(i);
//            Article entity = dto.toEntity();
//            articleList.add(entity);
//        }
        List<Article> articleList = dtos.stream().map(dto->dto.toEntity()).collect(Collectors.toList());

//        for(int i=0;i<articleList.size();i++){
//            Article article = articleList.get(i);
//            articleRepository.save(article);
//        }
        articleList.stream().forEach(article -> articleRepository.save(article));

        articleRepository.findById(-1L).orElseThrow(()->new IllegalArgumentException("문제발생"));
        return articleList;
    }
}
