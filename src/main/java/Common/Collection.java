package Common;

public class Collection {
   
	/**
	 * 对定长array数组正序排序 
	 * @param array
	 * @return
	 */
	 public static int[] shell_sort(int array[]){
		   int temp = 0;
		   int lenth=array.length;
		   int incre = lenth;		   
		   while(true){
		       incre = incre/2;
		       for(int k = 0;k<incre;k++){    //根据增量分为若干子序列
		           for(int i=k+incre;i<lenth;i+=incre){
	
		               for(int j=i;j>k;j-=incre){
		                   if(array[j]<array[j-incre]){
		                       temp = array[j-incre];
		                       array[j-incre] = array[j];
		                       array[j] = temp;
		                   }else{
		                       break;
		                   }
		               }
		           }
		       }
		       if(incre == 1){
		           break;
		       }
		   }
		   return array;
	 }
	 
}
