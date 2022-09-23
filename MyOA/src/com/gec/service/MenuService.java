package com.gec.service;

import com.gec.domain.SysPermission;

import java.util.List;

public interface MenuService {
    List<SysPermission> findSysPermissionWhereTypeIsMenu();

    List<SysPermission> findMenuChildrensByParentID(Long id);

    List<SysPermission> findMenuChildrensByParentIDLikePermission(Long id);
}
