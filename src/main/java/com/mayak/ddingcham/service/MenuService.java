package com.mayak.ddingcham.service;

import com.mayak.ddingcham.domain.*;
import com.mayak.ddingcham.dto.MenuDTO;
import com.mayak.ddingcham.dto.MenuDTOToUpload;
import com.mayak.ddingcham.dto.MenuOutputDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class MenuService {
    @Autowired
    MenuRepository menuRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    S3Uploader s3Uploader;


    public void createMenu(MenuDTO menuDTO, User user) {
        log.debug("Menu DTO : {}", menuDTO);
        Store store = storeRepository.findByUser(user).get();
        Menu menu = menuDTO.toDomain(store);
        log.debug("Menu : {}", menu);
        store.addMenu(menu);
        storeRepository.save(store);
    }

    @Transactional
    public void createMenu(MenuDTOToUpload menuDTO, Store store) throws IOException {
        String menuImgUrl = s3Uploader.upload(menuDTO.getFile(), "static");
        Menu menu = menuDTO.toDomain(store, menuImgUrl);
        store.addMenu(menu);
        storeRepository.save(store);
    }

    //todo cacheable, cacheEvict on reservation registration
    public List<MenuOutputDTO> getLastUsedMenusInStore(Store store) {
        return null;
//        return store.getUsedMenuOutputDTOList();
    }

    //todo cacheable, cacheEvict on reservation registration
    public List<MenuOutputDTO> findAllMenuInStore(Store store) {
        return null;
//        return store.getMenuOutputDTOList();
    }

    @Transactional
    public Menu deleteMenu(Store store, long menuId) {
        Menu menu = menuRepository
                .findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        store.removeMenu(menu);
        return menu;
    }
    private Store getStoreByStoreId(long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(
                        () -> new EntityNotFoundException("No Search Store By storeId : " + storeId)
                );
    }
}
