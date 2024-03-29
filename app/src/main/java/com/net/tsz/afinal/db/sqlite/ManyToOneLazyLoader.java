package com.net.tsz.afinal.db.sqlite;

import com.net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 一对多延迟加载类
 * Created by pwy on 13-7-25.
 * @param <O> 宿主实体的class
 * @param <M> 多放实体class
 */
public class ManyToOneLazyLoader<M,O> {
    M manyEntity;
    Class<M> manyClazz;
    Class<O> oneClazz;
    FinalDb db;
    public ManyToOneLazyLoader(M manyEntity, Class<M> manyClazz, Class<O> oneClazz, FinalDb db){
        this.manyEntity = manyEntity;
        this.manyClazz = manyClazz;
        this.oneClazz = oneClazz;
        this.db = db;
    }
    O oneEntity;
    boolean hasLoaded = false;

    /**
     * 如果数据未加载，则调用loadManyToOne填充数据
     * @return
     */
    public O get(){
        if(oneEntity==null && !hasLoaded){
            this.db.loadManyToOne(this.manyEntity,this.manyClazz,this.oneClazz);
            hasLoaded = true;
        }
        return oneEntity;
    }
    public void set(O value){
        oneEntity = value;
    }

}
