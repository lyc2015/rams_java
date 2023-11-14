import jp.co.lyc.cms.controller.SalesProfitController;

public class SalesProfitControllerTest {

	public static void main(String[] args) {
		SalesProfitController controller = new SalesProfitController();

		{
			int dif = controller.getMonthDif("202302", "202302", "202301", "202303");
			System.out.println("dif=" + ( dif == 1));
		}
		{
			int dif = controller.getMonthDif("202302", "202302", "202302", "202303");
			System.out.println("dif=" + ( dif == 1));
		}
		{
			int dif = controller.getMonthDif("202302", "202302", "202303", "202303");
			System.out.println("dif=" + ( dif == 0));
		}
		{
			int dif = controller.getMonthDif("202302", "202306", "202301", "202306");
			System.out.println("dif=" + ( dif == 5));
		}
		{
			int dif = controller.getMonthDif("202302", "202306", "202303", "202306");
			System.out.println("dif=" + ( dif == 4));
		}

		{
			int dif = controller.getMonthDif("202302", "202306", "202307", "202306");
			System.out.println("dif=" + ( dif == 0));
		}

		{
			int dif = controller.getMonthDif("202206", "202306", "202201", "202306");
			System.out.println("dif=" + dif + "," + ( dif == 13));
		}
		
		{
			int dif = controller.getMonthDif("202206", "202306", "202208", "202306");
			System.out.println("dif=" + dif + "," + ( dif == 11));
		}

		{
			int dif = controller.getMonthDif("202206", "202306", "202307", "202306");
			System.out.println("dif=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDif("202203", "202304", "202304", "202304");
			System.out.println("dif=" + dif + "," + ( dif == 1));
		}

		{
			int dif = controller.getMonthDif("202203", "202304", "202303", "202304");
			System.out.println("dif=" + dif + "," + ( dif == 2));
		}

		{
			int dif = controller.getMonthDif("202203", "202304", "202305", "202304");
			System.out.println("dif=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDifFix("202306", "202306", "202211");
			System.out.println("fix dif 1=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202306", "202306", "202306");
			System.out.println("fix dif 2=" + dif + "," + ( dif == 1));
		}
		{
			int dif = controller.getMonthDifFix("202306", "202306", "202311");
			System.out.println("fix dif 3=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202302", "202304", "202211");
			System.out.println("fix dif 4=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDifFix("202302", "202304", "202306");
			System.out.println("fix dif 5=" + dif + "," + ( dif == 0));
		}


		{
			int dif = controller.getMonthDifFix("202302", "202307", "202211");
			System.out.println("fix dif 6=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDifFix("202302", "202307", "202311");
			System.out.println("fix dif 7=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDifFix("202302", "2023012", "202211");
			System.out.println("fix dif 8=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202302", "2023012", "202406");
			System.out.println("fix dif 9=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202307", "2023012", "202306");
			System.out.println("fix dif 10=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202307", "2023012", "202406");
			System.out.println("fix dif 11=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202307", "2023012", "202306");
			System.out.println("fix dif 12=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("202307", "2023012", "202311");
			System.out.println("fix dif 13=" + dif + "," + ( dif == 1));
		}

		{
			int dif = controller.getMonthDifFix("2022012", "202304", "202206");
			System.out.println("fix dif 14=" + dif + "," + ( dif == 0));
		}

		{
			int dif = controller.getMonthDifFix("2022012", "202304", "202211");
			System.out.println("fix dif 15=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("2022012", "202304", "202306");
			System.out.println("fix dif 16=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("2022012", "202307", "202211");
			System.out.println("fix dif 17=" + dif + "," + ( dif == 0));
		}
		{
			int dif = controller.getMonthDifFix("2022012", "202307", "202306");
			System.out.println("fix dif 18=" + dif + "," + ( dif == 1));
		}
		{
			int dif = controller.getMonthDifFix("2022012", "202307", "202306");
			System.out.println("fix dif 19=" + dif + "," + ( dif == 1));
		}
		{
			int dif = controller.getMonthDifFix("2022012", "202307", "202311");
			System.out.println("fix dif 20=" + dif + "," + ( dif == 0));
		}
	}
}
