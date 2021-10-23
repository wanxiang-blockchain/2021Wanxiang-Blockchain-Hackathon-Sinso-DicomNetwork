# 2021Wanxiang-Blockchain-Hackathon-Sinso-DicomNetwork
DicomNetwork是永久免费和开源的。
医生和患者都可以只使用电子邮件地址进行协作，无需注册或登录。
DicomNetwork，是SINSO去中心化医疗基础设施的一部分，它是SINSO的Getway组件面向
医学影像的应用，DicomNetwork内置了处理医学影像的标准通讯协议，并且顺利桥接到
Filecoin生态，本质上DicomNetwork是一个医学影像存储和展现的SDK，具有良好的扩展性。

DicomNetwork is an open source public welfare project. It is a network of medical 
image storage and diagnosis platform based on Filecoin. DicomNetwork is 
permanently free and open source.
Both doctors and patients can collaborate using only email addresses without 
registering or logging in.
DicomNetwork, a part of SINSO's decentralized medical infrastructure, is a medical 
imaging application of SINSO's Getway component. DicomNetwork has built-in 
standard communication protocols for processing medical images, and has 
successfully bridged to the Filecoin ecosystem. In essence, DicomNetwork is an SDK 
for medical image storage and presentation, with good scalability.

画册
https://ipacs-film.oss-cn-shenzhen.aliyuncs.com/dicom_ui/DicomNetwork.pdf
视频地址
https://ipacs-film.oss-cn-shenzhen.aliyuncs.com/dicom_ui/sinso.m4v
演示地址
https://120.27.29.91:8888/login

部署：
前端页面可完全部署与IPFS上面（通过Fleek可便捷操作），因访问速度问题，演示版本网站页面存放于阿里云OSS空间当中，作用是相同效果。
后端部分部署于服务器当中，这部分接下来用智能合约形式实现，完全实现去中心化部分

拓展：
目前流程为患者得到自己影像数据后，上传到IPFS网络，通过平台找医生通过邮件方式沟通完成问诊咨询流程。
之后可加入sinso 激励机制，对患者使用自己数据进行奖励，调动患者维护自身数据的积极性。
加入人工智能模块，对患者上传数据进行机器阅读，最后根据分析内容推荐医生进行最后判断
