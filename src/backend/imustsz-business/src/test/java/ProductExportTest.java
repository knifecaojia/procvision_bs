import com.imustsz.collect.domain.*;
import com.imustsz.collect.mapper.CollectionManageMapper;
import com.imustsz.collect.service.impl.CollectionImageExportService;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.common.exception.ServiceException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.mapping.*;
import io.minio.StatObjectResponse;
import okhttp3.Headers;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.lang.reflect.Proxy;
public class ProductExportTest {
 static void check(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 static class Storage extends MinioUtils {
  byte[] bytes=new byte[81920]; boolean fail;
  Storage(){super(null);new Random(5).nextBytes(bytes);}
  public StatObjectResponse getObjectStat(String name){return new StatObjectResponse(Headers.of("Content-Length","81920","Content-Type","image/jpeg","Last-Modified","Sun, 13 Sep 2026 12:00:00 GMT","ETag","test"),"bucket",null,name);}
  public InputStream getFileInputStream(String bucket,String name){if(fail)throw new ServiceException("missing");return new ByteArrayInputStream(bytes);}
 }
 public static void main(String[] args)throws Exception {
  Configuration c=new Configuration();c.getTypeAliasRegistry().registerAlias("BizDataCollection",BizDataCollection.class);
  for(String file:Arrays.asList("BizDataCollectionMapper","CollectionManageMapper")){String resource="mapper/collection/"+file+".xml";try(InputStream in=Files.newInputStream(Paths.get(args[0],resource))){new XMLMapperBuilder(in,c,resource,c.getSqlFragments()).parse();}}
  for(int type=0;type<=1;type++){
   BizDataCollection r=new BizDataCollection();r.setType(type);r.setDatasetId(17);r.setData("sample");r.setCoordsInfo("");r.setLabelImage("");
   if(type==1){r.setProductModel("model");r.setProductBatch("batch");r.setProductTime(new Date());r.setProcessNum("P01");r.setOtherInfo("info");}
   BoundSql b=c.getMappedStatement("com.imustsz.collect.mapper.BizDataCollectionMapper.insertBizDataCollection").getBoundSql(r);
   String sql=b.getSql().replaceAll("\\s+"," ");String[] columns=sql.substring(sql.indexOf('(')+1,sql.indexOf(')')).split(",");
   check(columns.length==b.getParameterMappings().size(),"insert arity");
   for(int i=0;i<columns.length;i++)check(columns[i].trim().replace("_","").equalsIgnoreCase(b.getParameterMappings().get(i).getProperty()),"insert wrong column "+columns[i]);
   BoundSql update=c.getMappedStatement("com.imustsz.collect.mapper.BizDataCollectionMapper.updateBizDataCollection").getBoundSql(r);
   check(update.getSql().contains("product_time")== (type==1),"type0 metadata update changed");
  }
  CollectionManageQuery q=new CollectionManageQuery();q.setBeginProductTime("2026-09-10 00:00:00");q.setEndProductTime("2026-09-10 23:59:59");q.setProductModel("A");q.validate();
  Map<String,Object> p=new HashMap<>();p.put("q",q);p.put("ids",Arrays.asList(1L,2L));
  String sql=c.getMappedStatement("com.imustsz.collect.mapper.CollectionManageMapper.selectManagedExport").getBoundSql(p).getSql().replaceAll("\\s+"," ");
  check(sql.contains("product_time >=")&&sql.contains("product_time <=")&&sql.contains("type = 1")&&sql.contains(" IN "),"export filtering: "+sql);
  q.setBeginProductTime("2026-02-30 00:00:00");try{q.validate();throw new AssertionError("invalid date accepted");}catch(ServiceException expected){} q.setBeginProductTime("2026-09-10 00:00:00");
  List<BizDataCollection> rows=new ArrayList<>();
  for(long id=1;id<=2;id++){BizDataCollection r=new BizDataCollection();r.setId(id);r.setType(1);r.setData("same");r.setImagePath("[\"original.jpg\",\"thumb.jpg\"]");r.setProductModel(id==1?"型号A":"../../CON");r.setProductBatch("批次B");r.setProcessNum("P01");rows.add(r);}
  CollectionManageMapper mapper=(CollectionManageMapper)Proxy.newProxyInstance(ProductExportTest.class.getClassLoader(),new Class[]{CollectionManageMapper.class},(o,m,a)->rows);
  Storage storage=new Storage();CollectionExportRequest request=new CollectionExportRequest();request.setIds(Arrays.asList(1L,2L));request.setQuery(q);
  CollectionImageExportService service=new CollectionImageExportService(mapper,storage,"bucket",1000000);
  Path zip=service.createArchive(request);
  try(ZipFile z=new ZipFile(zip.toFile())){check(z.size()==3,"2 originals and manifest");Enumeration<? extends ZipEntry> entries=z.entries();while(entries.hasMoreElements()){ZipEntry e=entries.nextElement();check(!e.getName().startsWith("/")&&!Arrays.asList(e.getName().split("/")).contains(".."),"unsafe path");if(!e.getName().equals("manifest.json"))check(Arrays.equals(storage.bytes,z.getInputStream(e).readAllBytes()),"original bytes changed");}check(z.getEntry("型号A/批次B/P01/1_same.jpg")!=null,"directory layout");}finally{Files.delete(zip);}
  rows.get(1).setType(0);try{service.createArchive(request);throw new AssertionError("type0 exported");}catch(ServiceException expected){}rows.get(1).setType(1);
  try{new CollectionImageExportService(mapper,storage,"bucket",100).createArchive(request);throw new AssertionError("limit ignored");}catch(ServiceException expected){}
  storage.fail=true;try{service.createArchive(request);throw new AssertionError("missing image ignored");}catch(ServiceException expected){}
  System.out.println("PASS: type0/type1 insert column alignment; optional metadata updates; production datetime filters/validation; original-byte ZIP, classified directories, shared-key records, safe paths, type0 rejection, size limit and IO failure.");
 }
}
