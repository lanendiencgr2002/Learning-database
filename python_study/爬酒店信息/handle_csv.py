import os
import analyze_email_patterns
import generate_missing_emails
import filter_and_merge
import 补充总经理信息
import 处理总经理位置

def get_paths():
    """获取所有需要的文件路径"""
    base_dir = os.path.dirname(os.path.abspath(__file__))
    
    paths = {
        'csv目录': os.path.join(base_dir, 'csv文件夹21个公司'),
        '生成邮件后的csv文件目录': os.path.join(base_dir, '生成邮件后的csv文件目录'),
        'final_filtered_results': os.path.join(base_dir, 'final_filtered_results.csv')
    }
    
    return paths

def analyze_emails(csv_dir):
    """分析邮件格式"""
    print("开始分析邮件格式...")
    analyze_email_patterns.main(csv_dir)
    print("邮件格式分析完成")

def generate_emails(csv_dir):
    """生成缺失的电子邮件"""
    print("开始生成缺失的电子邮件...")
    generate_missing_emails.main(csv_dir)
    print("缺失邮件生成完成")

def filter_and_merge_files(output_dir):
    """过滤和合并CSV文件"""
    print("开始过滤和合并文件...")
    filter_and_merge.main(output_dir)
    print("文件过滤合并完成")

def supplement_gm_info(final_results_path):
    """补充总经理信息到JSON"""
    print("开始补充总经理信息...")
    补充总经理信息.main(final_results_path)
    print("总经理信息补充完成")

def process_gm_positions(final_results_path):
    """处理总经理位置到CSV"""
    print("开始处理总经理位置...")
    处理总经理位置.main(final_results_path)
    print("总经理位置处理完成")

def process_all():
    """处理所有步骤的主函数"""
    try:
        # 获取所有路径
        paths = get_paths()
        
        # 确保所有目录存在
        for path in paths.values():
            dir_path = path if path.endswith('.csv') else path
            os.makedirs(os.path.dirname(dir_path), exist_ok=True)
        
        # 按顺序执行所有步骤
        analyze_emails(paths['csv目录'])
        generate_emails(paths['csv目录'])
        filter_and_merge_files(paths['生成邮件后的csv文件目录'])
        supplement_gm_info(paths['final_filtered_results'])
        process_gm_positions(paths['final_filtered_results'])
        
        print("所有处理步骤已完成！")
        return True
        
    except Exception as e:
        print(f"处理过程中出现错误: {str(e)}")
        return False

if __name__ == "__main__":
    process_all()
