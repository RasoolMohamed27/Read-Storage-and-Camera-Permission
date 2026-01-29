#!/usr/bin/env python3
"""
Figma File Comparison Tool

This script compares two Figma design files and generates an Excel report
with screen names, images, and comments for each screen.

Usage:
    python figma_comparison.py --token YOUR_FIGMA_TOKEN --file1 FILE_KEY_1 --file2 FILE_KEY_2
"""

import argparse
import requests
import json
import os
from openpyxl import Workbook
from openpyxl.drawing.image import Image as XLImage
from openpyxl.styles import Font, Alignment, PatternFill
from openpyxl.utils import get_column_letter
from io import BytesIO
from PIL import Image
import sys


class FigmaComparator:
    def __init__(self, api_token):
        """Initialize the Figma comparator with API token."""
        self.api_token = api_token
        self.base_url = "https://api.figma.com/v1"
        self.headers = {"X-Figma-Token": api_token}
        
    def extract_file_key(self, url):
        """Extract file key from Figma URL."""
        # URL format: https://www.figma.com/design/FILE_KEY/...
        if "figma.com/design/" in url:
            parts = url.split("/design/")[1].split("/")[0].split("?")[0]
            return parts
        elif "figma.com/file/" in url:
            parts = url.split("/file/")[1].split("/")[0].split("?")[0]
            return parts
        else:
            return url  # Assume it's already a file key
    
    def get_file_data(self, file_key):
        """Fetch file data from Figma API."""
        url = f"{self.base_url}/files/{file_key}"
        print(f"Fetching file data for {file_key}...")
        
        try:
            response = requests.get(url, headers=self.headers)
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            print(f"Error fetching file data: {e}")
            return None
    
    def get_file_comments(self, file_key):
        """Fetch comments from Figma file."""
        url = f"{self.base_url}/files/{file_key}/comments"
        print(f"Fetching comments for {file_key}...")
        
        try:
            response = requests.get(url, headers=self.headers)
            response.raise_for_status()
            return response.json().get("comments", [])
        except requests.exceptions.RequestException as e:
            print(f"Error fetching comments: {e}")
            return []
    
    def extract_screens(self, file_data):
        """Extract screen/frame information from file data."""
        screens = []
        
        def traverse_node(node, parent_name=""):
            """Recursively traverse the node tree to find frames/screens."""
            node_type = node.get("type", "")
            node_name = node.get("name", "")
            node_id = node.get("id", "")
            
            # Consider FRAME, COMPONENT, and CANVAS as potential screens
            if node_type in ["FRAME", "COMPONENT", "COMPONENT_SET"]:
                screens.append({
                    "id": node_id,
                    "name": node_name,
                    "type": node_type,
                    "parent": parent_name
                })
            
            # Recursively check children
            children = node.get("children", [])
            for child in children:
                traverse_node(child, node_name if node_type == "CANVAS" else parent_name)
        
        # Start traversal from document
        document = file_data.get("document", {})
        traverse_node(document)
        
        return screens
    
    def get_screen_images(self, file_key, node_ids):
        """Get image URLs for specific nodes."""
        if not node_ids:
            return {}
        
        # Figma API limits to 100 IDs per request
        batch_size = 50
        all_images = {}
        
        for i in range(0, len(node_ids), batch_size):
            batch = node_ids[i:i + batch_size]
            ids_param = ",".join(batch)
            url = f"{self.base_url}/images/{file_key}?ids={ids_param}&format=png&scale=2"
            
            print(f"Fetching images for batch {i//batch_size + 1}...")
            
            try:
                response = requests.get(url, headers=self.headers)
                response.raise_for_status()
                images = response.json().get("images", {})
                all_images.update(images)
            except requests.exceptions.RequestException as e:
                print(f"Error fetching images: {e}")
        
        return all_images
    
    def download_image(self, url):
        """Download image from URL and return PIL Image object."""
        try:
            response = requests.get(url)
            response.raise_for_status()
            return Image.open(BytesIO(response.content))
        except Exception as e:
            print(f"Error downloading image: {e}")
            return None
    
    def compare_files(self, file_key1, file_key2):
        """Compare two Figma files and return comparison data."""
        print("\n=== Starting Figma File Comparison ===\n")
        
        # Fetch file data
        file_data1 = self.get_file_data(file_key1)
        file_data2 = self.get_file_data(file_key2)
        
        if not file_data1 or not file_data2:
            print("Error: Could not fetch file data")
            return None
        
        file_name1 = file_data1.get("name", "File 1")
        file_name2 = file_data2.get("name", "File 2")
        
        print(f"File 1: {file_name1}")
        print(f"File 2: {file_name2}\n")
        
        # Extract screens
        screens1 = self.extract_screens(file_data1)
        screens2 = self.extract_screens(file_data2)
        
        print(f"Found {len(screens1)} screens in File 1")
        print(f"Found {len(screens2)} screens in File 2\n")
        
        # Fetch comments
        comments1 = self.get_file_comments(file_key1)
        comments2 = self.get_file_comments(file_key2)
        
        # Create comment mapping by node ID
        comments_map1 = {}
        for comment in comments1:
            node_id = comment.get("client_meta", {}).get("node_id")
            if node_id:
                if node_id not in comments_map1:
                    comments_map1[node_id] = []
                comments_map1[node_id].append(comment.get("message", ""))
        
        comments_map2 = {}
        for comment in comments2:
            node_id = comment.get("client_meta", {}).get("node_id")
            if node_id:
                if node_id not in comments_map2:
                    comments_map2[node_id] = []
                comments_map2[node_id].append(comment.get("message", ""))
        
        # Get image URLs
        node_ids1 = [s["id"] for s in screens1]
        node_ids2 = [s["id"] for s in screens2]
        
        images1 = self.get_screen_images(file_key1, node_ids1)
        images2 = self.get_screen_images(file_key2, node_ids2)
        
        # Create comparison data
        comparison_data = {
            "file1": {
                "name": file_name1,
                "key": file_key1,
                "screens": screens1,
                "images": images1,
                "comments": comments_map1
            },
            "file2": {
                "name": file_name2,
                "key": file_key2,
                "screens": screens2,
                "images": images2,
                "comments": comments_map2
            }
        }
        
        return comparison_data
    
    def generate_excel_report(self, comparison_data, output_file="figma_comparison.xlsx"):
        """Generate Excel report with comparison results."""
        print("\n=== Generating Excel Report ===\n")
        
        wb = Workbook()
        ws = wb.active
        ws.title = "Figma Comparison"
        
        # Set column widths
        ws.column_dimensions['A'].width = 30
        ws.column_dimensions['B'].width = 40
        ws.column_dimensions['C'].width = 30
        ws.column_dimensions['D'].width = 40
        ws.column_dimensions['E'].width = 40
        
        # Header style
        header_fill = PatternFill(start_color="4472C4", end_color="4472C4", fill_type="solid")
        header_font = Font(bold=True, color="FFFFFF", size=12)
        
        # Add headers
        headers = [
            "Screen Name",
            f"{comparison_data['file1']['name']} Image",
            "Screen Name",
            f"{comparison_data['file2']['name']} Image",
            "Comments"
        ]
        
        for col_num, header in enumerate(headers, 1):
            cell = ws.cell(row=1, column=col_num)
            cell.value = header
            cell.fill = header_fill
            cell.font = header_font
            cell.alignment = Alignment(horizontal="center", vertical="center")
        
        # Create a mapping of screen names
        screens1_dict = {s["name"]: s for s in comparison_data["file1"]["screens"]}
        screens2_dict = {s["name"]: s for s in comparison_data["file2"]["screens"]}
        
        # Get all unique screen names
        all_screen_names = sorted(set(screens1_dict.keys()) | set(screens2_dict.keys()))
        
        print(f"Processing {len(all_screen_names)} unique screen names...")
        
        # Create images directory
        images_dir = "figma_comparison_images"
        os.makedirs(images_dir, exist_ok=True)
        
        current_row = 2
        
        for screen_name in all_screen_names:
            print(f"Processing: {screen_name}")
            
            # Set row height
            ws.row_dimensions[current_row].height = 200
            
            # File 1 screen
            if screen_name in screens1_dict:
                screen1 = screens1_dict[screen_name]
                ws.cell(row=current_row, column=1).value = screen_name
                
                # Add image if available
                image_url = comparison_data["file1"]["images"].get(screen1["id"])
                if image_url:
                    try:
                        img = self.download_image(image_url)
                        if img:
                            # Resize image to fit in cell
                            img.thumbnail((300, 300), Image.Resampling.LANCZOS)
                            img_path = os.path.join(images_dir, f"file1_{screen1['id']}.png")
                            img.save(img_path)
                            
                            # Add to Excel
                            xl_img = XLImage(img_path)
                            xl_img.width = 250
                            xl_img.height = 250
                            ws.add_image(xl_img, f'B{current_row}')
                    except Exception as e:
                        print(f"Error adding image: {e}")
                        ws.cell(row=current_row, column=2).value = "Image not available"
            
            # File 2 screen
            if screen_name in screens2_dict:
                screen2 = screens2_dict[screen_name]
                ws.cell(row=current_row, column=3).value = screen_name
                
                # Add image if available
                image_url = comparison_data["file2"]["images"].get(screen2["id"])
                if image_url:
                    try:
                        img = self.download_image(image_url)
                        if img:
                            # Resize image to fit in cell
                            img.thumbnail((300, 300), Image.Resampling.LANCZOS)
                            img_path = os.path.join(images_dir, f"file2_{screen2['id']}.png")
                            img.save(img_path)
                            
                            # Add to Excel
                            xl_img = XLImage(img_path)
                            xl_img.width = 250
                            xl_img.height = 250
                            ws.add_image(xl_img, f'D{current_row}')
                    except Exception as e:
                        print(f"Error adding image: {e}")
                        ws.cell(row=current_row, column=4).value = "Image not available"
            
            # Add comments
            comments = []
            if screen_name in screens1_dict:
                screen1_id = screens1_dict[screen_name]["id"]
                if screen1_id in comparison_data["file1"]["comments"]:
                    comments.extend([f"File 1: {c}" for c in comparison_data["file1"]["comments"][screen1_id]])
            
            if screen_name in screens2_dict:
                screen2_id = screens2_dict[screen_name]["id"]
                if screen2_id in comparison_data["file2"]["comments"]:
                    comments.extend([f"File 2: {c}" for c in comparison_data["file2"]["comments"][screen2_id]])
            
            if comments:
                ws.cell(row=current_row, column=5).value = "\n".join(comments)
                ws.cell(row=current_row, column=5).alignment = Alignment(wrap_text=True, vertical="top")
            
            # Alignment
            for col in [1, 3]:
                ws.cell(row=current_row, column=col).alignment = Alignment(vertical="top")
            
            current_row += 1
        
        # Save workbook
        wb.save(output_file)
        print(f"\n✓ Excel report saved to: {output_file}")
        print(f"✓ Images saved to: {images_dir}/")
        
        return output_file


def main():
    parser = argparse.ArgumentParser(
        description="Compare two Figma design files and generate Excel report"
    )
    parser.add_argument(
        "--token",
        required=True,
        help="Figma API token (get from https://www.figma.com/developers/api#access-tokens)"
    )
    parser.add_argument(
        "--file1",
        required=True,
        help="First Figma file URL or file key"
    )
    parser.add_argument(
        "--file2",
        required=True,
        help="Second Figma file URL or file key"
    )
    parser.add_argument(
        "--output",
        default="figma_comparison.xlsx",
        help="Output Excel file name (default: figma_comparison.xlsx)"
    )
    
    args = parser.parse_args()
    
    # Create comparator
    comparator = FigmaComparator(args.token)
    
    # Extract file keys from URLs
    file_key1 = comparator.extract_file_key(args.file1)
    file_key2 = comparator.extract_file_key(args.file2)
    
    print(f"File 1 Key: {file_key1}")
    print(f"File 2 Key: {file_key2}")
    
    # Compare files
    comparison_data = comparator.compare_files(file_key1, file_key2)
    
    if comparison_data:
        # Generate report
        comparator.generate_excel_report(comparison_data, args.output)
        print("\n✓ Comparison complete!")
    else:
        print("\n✗ Comparison failed")
        sys.exit(1)


if __name__ == "__main__":
    main()
