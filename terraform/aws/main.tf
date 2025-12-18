terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "health-tourism-terraform-state"
    key    = "terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.aws_region
}

# EKS Cluster
module "eks_cluster" {
  source = "./modules/eks"
  
  cluster_name    = "health-tourism"
  cluster_version = "1.28"
  node_count      = 3
  instance_type   = "t3.medium"
  
  tags = {
    Environment = "production"
    Project     = "health-tourism"
  }
}

# RDS MySQL
module "rds" {
  source = "./modules/rds"
  
  db_instance_class    = "db.t3.medium"
  allocated_storage    = 100
  engine_version       = "8.0"
  db_name              = "health_tourism"
  username             = "admin"
  password             = var.db_password
  
  tags = {
    Environment = "production"
    Project     = "health-tourism"
  }
}

# S3 for file storage
resource "aws_s3_bucket" "health_tourism_storage" {
  bucket = "health-tourism-storage-${random_id.bucket_suffix.hex}"
  
  tags = {
    Environment = "production"
    Project     = "health-tourism"
  }
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

# Secrets Manager
resource "aws_secretsmanager_secret" "database" {
  name = "health-tourism/database"
}

resource "aws_secretsmanager_secret_version" "database" {
  secret_id = aws_secretsmanager_secret.database.id
  secret_string = jsonencode({
    username = "admin"
    password = var.db_password
    host     = module.rds.db_endpoint
    port     = 3306
  })
}

variable "aws_region" {
  description = "AWS region"
  default     = "us-east-1"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

output "eks_cluster_endpoint" {
  value = module.eks_cluster.cluster_endpoint
}

output "rds_endpoint" {
  value = module.rds.db_endpoint
}
