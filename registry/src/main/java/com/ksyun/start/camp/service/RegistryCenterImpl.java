package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.RegistryVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RegistryCenterImpl implements RegistryCenter {


    //服务发现的时候需要轮询服务，point表示为轮询的指针。
    private AtomicInteger point = new AtomicInteger(0);
    //所有存储数据在内存中。key为serviceName，value为RegistryVo的set。表示当前服务名的所有微服务。
    private Map<String, Set<RegistryVo>> registerMap=new ConcurrentHashMap<>();


    public Map<String, Set<RegistryVo>> getRegisterMap() {
        return registerMap;
    }


    @Override
    public boolean registryService(RegistryVo registryVo) {
        //1.获取服务名。
        String serviceName = registryVo.getServiceName();
        //2.通过服务名查找对应set。如果没找到这个serviceName就创建新的set。
        Set<RegistryVo> registryVoSet
                = registerMap.computeIfAbsent(serviceName, v -> Collections.synchronizedSet(new LinkedHashSet<RegistryVo>()));

        //3.把新服务注册到注册中心。
        if(!registryVoSet.contains(registryVo)){
            //如果set没有当前的registryVo表示这个是正确的请求，注册成功。
            registryVoSet.add(registryVo);
            return true;
        }else return false;//当前已经有了这个registryVo了。不可能会重复注册的情况。


    }

    @Override
    public boolean unRegistryService(RegistryVo registryVo) {
        //1.找到服务名的set。
        String serviceName = registryVo.getServiceName();
        Set<RegistryVo> registryVoSet = registerMap.get(serviceName);
        if(registryVoSet==null){
            return false;
        }

        //2.找到一致的服务就删除。
        for ( RegistryVo oneRegistryVo : registryVoSet) {
            if(oneRegistryVo.getServiceName().equals(registryVo.getServiceName())
                    && oneRegistryVo.getServiceId().equals(registryVo.getServiceId())
                    && oneRegistryVo.getIpAddress().equals(registryVo.getIpAddress())
                    && oneRegistryVo.getPort().equals(registryVo.getPort())){
                registryVoSet.remove(oneRegistryVo);
                return true;//正常删除
            }
        }
        return false;//未找到，删除失败
    }

    //发现所有服务
    @Override
    public List<RegistryVo> discoveryService() {
        //1.新建返回的list，保存所有服务
        List<RegistryVo> allServiceList =new ArrayList<>();
        Set<String> serviceNames = registerMap.keySet();

        //2.循环表里每个set，把里面微服务都加到list中
        for (String serviceName:serviceNames) {
            Set<RegistryVo> registryVoSet = registerMap.get(serviceName);
            allServiceList.addAll(registryVoSet);
        }

        //3.返回list
        return allServiceList;
    }

    //根据服务名负载均衡返回相应的一个服务
    @Override
    public  List<RegistryVo> discoveryServiceByName(String serviceName) {
        //1.获得当前服务名对应的set
        Set<RegistryVo> registryVoSet = registerMap.get(serviceName);
        if(registryVoSet==null){
            return null;
        }
        if(registryVoSet.size()==0){
            return null;
        }
        //2.转换为list。
        List<RegistryVo> registryVoList =new ArrayList<>(registryVoSet);

        //3.负载均衡
        int currentPoint  = point.getAndIncrement() % registryVoList.size();

        //4.创建储存最终返回结果的list
        ArrayList<RegistryVo> registryVoListOne = new ArrayList<>();

        //5.从registryVoList中选择一个返回。
        registryVoListOne.add(registryVoList.get(currentPoint));
        return registryVoListOne;
    }

    //客服端定期发送请求，更新registryVo中的时间
    @Override
    public boolean heartbeat(RegistryVo registryVo) {
        //1.根据服务id查到对应的服务名称。
        List<RegistryVo> allServiceList = discoveryService();
        String serviceName="";
        for (RegistryVo r : allServiceList) {
            if(r.getServiceId().equals(registryVo.getServiceId())){
                serviceName=r.getServiceName();
                break;
            }
        }

        Set<RegistryVo> registryVoSet = registerMap.get(serviceName);

        //2.如果为空，表示这个服务从来没有注册过。可能是攻击请求，直接返回。
        if (registryVoSet==null){
            return false;
        }

//        //3.如果发送过来的心跳服务名为空就不注册。
//        if(registryVo.getServiceName()==null|| registryVo.getServiceName().equals("")){
//            return false;
//        }
//        if(registryVo.getServiceId()==null|| registryVo.getServiceId().equals("")){
//            return false;
//        }
//        if(registryVo.getIpAddress()==null|| registryVo.getIpAddress().equals("")){
//            return false;
//        }
//        if(registryVo.getPort()==null){
//            return false;
//        }

        //4.重写RegistryVo时间，表示进行了一次心跳。
        for (RegistryVo oneRegistryVo: registryVoSet) {
            if(
                    //oneRegistryVo.getServiceName().equals(registryVo.getServiceName())&&
                     oneRegistryVo.getServiceId().equals(registryVo.getServiceId())
                    && oneRegistryVo.getIpAddress().equals(registryVo.getIpAddress())
                    && oneRegistryVo.getPort().equals(registryVo.getPort())){
                oneRegistryVo.setRegisterTime(registryVo.getRegisterTime());
                return true;
            }
        }
        return false;


    }


}
