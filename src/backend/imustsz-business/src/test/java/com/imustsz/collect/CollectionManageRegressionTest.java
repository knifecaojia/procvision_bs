package com.imustsz.collect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.imustsz.collect.domain.*;
import com.imustsz.collect.mapper.CollectionManageMapper;
import com.imustsz.collect.service.impl.*;
import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.bean.MinioUtils;
import io.minio.StatObjectResponse;
import okhttp3.Headers;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

/** Standalone main; uses real encoders/MyBatis and fake storage/database, never business data. */
public class CollectionManageRegressionTest {
    static final ObjectMapper JSON = new ObjectMapper();
    public static void main(String[] args) throws Exception {
        Path directory = Paths.get(args[0]); Files.createDirectories(directory);
        Path source = directory.resolve("source.png"), low = directory.resolve("low.jpg"), high = directory.resolve("high.jpg"), png = directory.resolve("saved.png"), thumb = directory.resolve("thumb.png");
        BufferedImage input = new BufferedImage(1200,800,BufferedImage.TYPE_INT_ARGB);
        Random random = new Random(42);
        for(int y=0;y<800;y++) for(int x=0;x<1200;x++) input.setRGB(x,y,0xff000000 | random.nextInt(0xffffff));
        input.setRGB(0,0,0);
        ImageIO.write(input,"png",source.toFile());
        CollectionImageCodec.convert(source,low,thumb,"jpeg",25,60000000);
        CollectionImageCodec.convert(source,high,thumb,"jpeg",95,60000000);
        if(Files.size(high)<=Files.size(low)*2) throw new AssertionError("JPEG quality did not affect stored bytes");
        BufferedImage jpeg = ImageIO.read(high.toFile());
        if(jpeg.getWidth()!=1200||jpeg.getHeight()!=800)throw new AssertionError("Original dimensions changed");
        jpeg.flush();
        CollectionImageCodec.convert(source,png,thumb,"png",null,60000000);
        BufferedImage decoded=ImageIO.read(png.toFile());
        for(int y=0;y<800;y++)for(int x=0;x<1200;x++)if(input.getRGB(x,y)!=decoded.getRGB(x,y))throw new AssertionError("PNG pixels changed");
        decoded.flush(); input.flush();
        BufferedImage thumbnail=ImageIO.read(thumb.toFile());
        if(Math.max(thumbnail.getWidth(),thumbnail.getHeight())>800)throw new AssertionError("Thumbnail too large");
        thumbnail.flush();
        System.out.println("PASS: JPEG real quality 25/95 sizes="+Files.size(low)+"/"+Files.size(high)+"; PNG exact RGBA, dimensions and thumbnails.");

        FakeMapper mapper=new FakeMapper(); FakeStorage storage=new FakeStorage();
        storage.objects.put("shared-source.png",Files.readAllBytes(source));
        storage.objects.put("shared-thumb.png",Files.readAllBytes(thumb));
        CollectionManageService service=new CollectionManageService(mapper,storage,"bucket",256,60000000);
        CollectionStorageRequest request=new CollectionStorageRequest();request.setId(7L);request.setFormat("jpeg");request.setQuality(70);
        mapper.row=record(0);
        expectFailure(()->service.convert(request,"test"));
        if(storage.reads!=0||storage.uploads!=0||mapper.updates!=0)throw new AssertionError("type=0 touched storage or database");
        mapper.row=record(1);
        String label=mapper.row.getLabelImage(),coords=mapper.row.getCoordsInfo();
        service.convert(request,"test");
        if(!storage.objects.containsKey("shared-source.png")||!storage.objects.containsKey("shared-thumb.png"))throw new AssertionError("Original shared objects removed");
        if(!label.equals(mapper.row.getLabelImage())||!coords.equals(mapper.row.getCoordsInfo()))throw new AssertionError("Annotations changed");
        String[] names=JSON.readValue(mapper.row.getImagePath(),String[].class);
        if(!names[0].startsWith("collection/type1/7/")||!names[0].endsWith(".jpg")||!storage.types.get(names[0]).equals("image/jpeg"))throw new AssertionError("New file identity/content type invalid");
        int stored=storage.objects.size();mapper.row=record(1);mapper.conflict=true;
        expectFailure(()->service.convert(request,"test"));
        if(storage.objects.size()!=stored)throw new AssertionError("Conflict left unreferenced uploads");
        mapper.conflict=false;storage.failSecond=true;storage.uploads=0;
        expectFailure(()->service.convert(request,"test"));
        if(storage.objects.size()!=stored)throw new AssertionError("Partial upload not cleaned");
        storage.failSecond=false;
        Page<BizDataCollection> page=new Page<>(2,10);page.setTotal(37);page.add(record(1));mapper.page=page;
        if(service.list(new CollectionManageQuery())!=page || page.getTotal()!=37)throw new AssertionError("PageHelper total discarded");
        System.out.println("PASS: type=0 rejected before IO, isolated new objects, annotations unchanged, CAS conflict and partial upload cleanup, PageHelper total preserved.");

        Path large=directory.resolve("80mb.bmp"),largeTarget=directory.resolve("80mb.jpg"),largeThumb=directory.resolve("80mb-thumb.jpg");
        try {
            writeBmp(large,5000,5600);
            CollectionImageCodec.convert(large,largeTarget,largeThumb,"jpeg",85,60000000);
            BufferedImage original=ImageIO.read(largeTarget.toFile());
            if(original.getWidth()!=5000||original.getHeight()!=5600)throw new AssertionError("Large original resized");
            original.flush();
            System.out.println("PASS: "+Files.size(large)+" byte BMP converted at full 5000x5600 resolution.");
        } finally {Files.deleteIfExists(large);Files.deleteIfExists(largeTarget);Files.deleteIfExists(largeThumb);}
        sqlCases();
    }
    interface Operation {void run() throws Exception;}
    static void expectFailure(Operation op)throws Exception {try{op.run();throw new AssertionError("Expected failure");}catch(ServiceException|IOException expected){}}
    static BizDataCollection record(int type){BizDataCollection r=new BizDataCollection();r.setId(7L);r.setType(type);r.setData("连接器");r.setImagePath("[\"shared-source.png\",\"shared-thumb.png\"]");r.setLabelImage("label.png");r.setCoordsInfo("{\"x\":100}");return r;}
    static class FakeMapper implements CollectionManageMapper {
        BizDataCollection row; boolean conflict; int updates; List<BizDataCollection> page;
        public List<BizDataCollection> selectManagedExport(CollectionManageQuery q,List<Long> ids){return page;}
        public BizDataCollection selectManagedById(Long id){return row;}
        public List<BizDataCollection> selectManagedList(CollectionManageQuery q){return page;}
        public int replaceManagedImage(Long id,String oldPath,String newPath,String username){updates++;if(conflict||row.getType()!=1||!oldPath.equals(row.getImagePath()))return 0;row.setImagePath(newPath);return 1;}
    }
    static class FakeStorage extends MinioUtils {
        Map<String,byte[]> objects=new HashMap<>();Map<String,String> types=new HashMap<>();int reads,uploads;boolean failSecond;
        FakeStorage(){super(null);}
        @Override public StatObjectResponse getObjectStat(String name){reads++;return new StatObjectResponse(Headers.of("Content-Length",String.valueOf(objects.get(name).length),"Content-Type","image/png","Last-Modified","Sun, 13 Sep 2026 12:00:00 GMT","ETag","test"),"bucket",null,name);}
        @Override public InputStream getFileInputStream(String bucket,String key){reads++;return new ByteArrayInputStream(objects.get(key));}
        @Override public void uploadFile(String key,InputStream stream,long size,String type)throws Exception{uploads++;objects.put(key,stream.readAllBytes());types.put(key,type);if(failSecond&&uploads==2)throw new IOException("Injected upload failure");}
        @Override public void deleteFile(String key){objects.remove(key);}
        @Override public String getPresignedUrl(String key){return "https://example.invalid/"+key;}
    }
    static void sqlCases()throws Exception {
        Configuration c=new Configuration();String resource="mapper/collection/CollectionManageMapper.xml";
        try(InputStream in=CollectionManageRegressionTest.class.getClassLoader().getResourceAsStream(resource)){new XMLMapperBuilder(in,c,resource,c.getSqlFragments()).parse();}
        List<Object> cases=new ArrayList<>();
        for(String variant:Arrays.asList("all","day","combined","unassigned")){
            CollectionManageQuery q=new CollectionManageQuery();
            if(!variant.equals("all")){q.setBeginProductTime("2026-09-13 00:00:00");q.setEndProductTime("2026-09-13 00:00:00");}
            if(variant.equals("combined")){q.setData("连接");q.setDatasetId(2);}
            if(variant.equals("unassigned"))q.setDatasetId(0);
            q.validate();cases.add(sql(c,"selectManagedList",Collections.singletonMap("q",q),variant));
        }
        cases.add(sql(c,"selectManagedById",Collections.singletonMap("id",2L),"type0"));
        Map<String,Object> p=new HashMap<>();p.put("id",2L);p.put("oldPath","old");p.put("newPath","new");p.put("username","test");
        cases.add(sql(c,"replaceManagedImage",p,"type0"));
        p=new HashMap<>(p);p.put("id",1L);p.put("oldPath","wrong");cases.add(sql(c,"replaceManagedImage",p,"conflict"));
        p=new HashMap<>(p);p.put("oldPath","old");cases.add(sql(c,"replaceManagedImage",p,"success"));
        CollectionManageQuery bad=new CollectionManageQuery();bad.setBeginProductTime("2026-09-14 00:00:00");bad.setEndProductTime("2026-09-13 00:00:00");expectFailure(bad::validate);
        System.out.println("SQL_CASES="+JSON.writeValueAsString(cases));
    }
    static Map<String,Object> sql(Configuration c,String id,Object p,String variant){
        BoundSql b=c.getMappedStatement(CollectionManageMapper.class.getName()+"."+id).getBoundSql(p);List<Object> values=new ArrayList<>();
        for(ParameterMapping param:b.getParameterMappings())values.add(b.hasAdditionalParameter(param.getProperty())?b.getAdditionalParameter(param.getProperty()):c.newMetaObject(p).getValue(param.getProperty()));
        Map<String,Object> result=new LinkedHashMap<>();result.put("id",id);result.put("variant",variant);result.put("sql",b.getSql());result.put("params",values);return result;
    }
    static void writeBmp(Path path,int width,int height)throws IOException{
        int stride=(width*3+3)&~3;ByteBuffer h=ByteBuffer.allocate(54).order(ByteOrder.LITTLE_ENDIAN);
        h.put((byte)'B').put((byte)'M').putInt(54+stride*height).putInt(0).putInt(54).putInt(40).putInt(width).putInt(height).putShort((short)1).putShort((short)24).putInt(0).putInt(stride*height).putInt(2835).putInt(2835).putInt(0).putInt(0);
        try(OutputStream out=new BufferedOutputStream(Files.newOutputStream(path))){out.write(h.array());byte[] row=new byte[stride];for(int x=0;x<width;x++){row[x*3]=(byte)(x%256);row[x*3+1]=(byte)70;row[x*3+2]=(byte)120;}for(int y=0;y<height;y++)out.write(row);}
    }
}
