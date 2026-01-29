#!/usr/bin/env python3
"""
Unit tests for Figma comparison tool

Tests the core functionality without making actual API calls.
"""

import unittest
from unittest.mock import Mock, patch, MagicMock
import sys
import os
import requests

# Add parent directory to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from figma_comparison import FigmaComparator


class TestFigmaComparator(unittest.TestCase):
    """Test cases for FigmaComparator class."""
    
    def setUp(self):
        """Set up test fixtures."""
        self.comparator = FigmaComparator("test_token")
    
    def test_extract_file_key_from_design_url(self):
        """Test extracting file key from design URL."""
        url = "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev"
        file_key = self.comparator.extract_file_key(url)
        self.assertEqual(file_key, "bj0HKrEhnqolfdSVCk7dtp")
    
    def test_extract_file_key_from_file_url(self):
        """Test extracting file key from file URL."""
        url = "https://www.figma.com/file/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662"
        file_key = self.comparator.extract_file_key(url)
        self.assertEqual(file_key, "egUxaGuWWwJe0lnmsUvD6m")
    
    def test_extract_file_key_from_plain_key(self):
        """Test handling plain file key."""
        file_key = "ABC123DEF456"
        result = self.comparator.extract_file_key(file_key)
        self.assertEqual(result, file_key)
    
    def test_extract_screens_from_file_data(self):
        """Test screen extraction from file data."""
        mock_file_data = {
            "document": {
                "id": "0:0",
                "name": "Document",
                "type": "DOCUMENT",
                "children": [
                    {
                        "id": "1:1",
                        "name": "Page 1",
                        "type": "CANVAS",
                        "children": [
                            {
                                "id": "2:1",
                                "name": "Home Screen",
                                "type": "FRAME",
                                "children": []
                            },
                            {
                                "id": "2:2",
                                "name": "Login Screen",
                                "type": "FRAME",
                                "children": []
                            }
                        ]
                    }
                ]
            }
        }
        
        screens = self.comparator.extract_screens(mock_file_data)
        self.assertEqual(len(screens), 2)
        self.assertEqual(screens[0]["name"], "Home Screen")
        self.assertEqual(screens[1]["name"], "Login Screen")
        self.assertEqual(screens[0]["type"], "FRAME")
    
    def test_extract_screens_with_components(self):
        """Test screen extraction including components."""
        mock_file_data = {
            "document": {
                "id": "0:0",
                "name": "Document",
                "type": "DOCUMENT",
                "children": [
                    {
                        "id": "1:1",
                        "name": "Page 1",
                        "type": "CANVAS",
                        "children": [
                            {
                                "id": "2:1",
                                "name": "Button Component",
                                "type": "COMPONENT",
                                "children": []
                            },
                            {
                                "id": "2:2",
                                "name": "Card Component",
                                "type": "COMPONENT_SET",
                                "children": []
                            }
                        ]
                    }
                ]
            }
        }
        
        screens = self.comparator.extract_screens(mock_file_data)
        self.assertEqual(len(screens), 2)
        self.assertIn("COMPONENT", [s["type"] for s in screens])
        self.assertIn("COMPONENT_SET", [s["type"] for s in screens])
    
    @patch('figma_comparison.requests.get')
    def test_get_file_data_success(self, mock_get):
        """Test successful file data retrieval."""
        mock_response = Mock()
        mock_response.json.return_value = {"name": "Test File", "document": {}}
        mock_response.raise_for_status = Mock()
        mock_get.return_value = mock_response
        
        result = self.comparator.get_file_data("test_file_key")
        
        self.assertIsNotNone(result)
        self.assertEqual(result["name"], "Test File")
        mock_get.assert_called_once()
    
    @patch('figma_comparison.requests.get')
    def test_get_file_data_failure(self, mock_get):
        """Test failed file data retrieval."""
        mock_get.side_effect = requests.exceptions.RequestException("API Error")
        
        result = self.comparator.get_file_data("test_file_key")
        
        self.assertIsNone(result)
    
    @patch('figma_comparison.requests.get')
    def test_get_file_comments(self, mock_get):
        """Test comment retrieval."""
        mock_response = Mock()
        mock_response.json.return_value = {
            "comments": [
                {
                    "id": "1",
                    "message": "Test comment",
                    "client_meta": {"node_id": "2:1"}
                }
            ]
        }
        mock_response.raise_for_status = Mock()
        mock_get.return_value = mock_response
        
        comments = self.comparator.get_file_comments("test_file_key")
        
        self.assertEqual(len(comments), 1)
        self.assertEqual(comments[0]["message"], "Test comment")


class TestCommandLineInterface(unittest.TestCase):
    """Test command line interface."""
    
    def test_import_module(self):
        """Test that the module can be imported."""
        import figma_comparison
        self.assertTrue(hasattr(figma_comparison, 'FigmaComparator'))
        self.assertTrue(hasattr(figma_comparison, 'main'))


if __name__ == '__main__':
    # Run tests
    unittest.main(verbosity=2)
