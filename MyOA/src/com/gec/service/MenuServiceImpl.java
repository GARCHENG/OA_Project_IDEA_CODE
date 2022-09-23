package com.gec.service;

import com.gec.domain.SysPermission;
import com.gec.domain.SysPermissionExample;
import com.gec.mapper.SysPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> findSysPermissionWhereTypeIsMenu() {
        SysPermissionExample spe = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = spe.createCriteria();
        criteria.andTypeEqualTo("menu");
        List<SysPermission> sysPermissions = sysPermissionMapper.selectByExample(spe);
        return sysPermissions;
    }

    @Override
    public List<SysPermission> findMenuChildrensByParentID(Long id) {
        SysPermissionExample spe = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = spe.createCriteria();
        criteria.andParentidEqualTo(id);
        criteria.andTypeEqualTo("menu|permission");
        List<SysPermission> sysPermissions = sysPermissionMapper.selectByExample(spe);
        return sysPermissions;
    }
    @Override
    public List<SysPermission> findMenuChildrensByParentIDLikePermission(Long id) {
        SysPermissionExample spe = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = spe.createCriteria();
        criteria.andParentidEqualTo(id);
//        criteria.andTypeEqualTo("menu|permission");
        criteria.andTypeLike("%permission%");
        List<SysPermission> sysPermissions = sysPermissionMapper.selectByExample(spe);
        return sysPermissions;
    }
}
